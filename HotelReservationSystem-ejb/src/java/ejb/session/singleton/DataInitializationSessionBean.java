/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.ReservationRoomSessionBeanLocal;
import ejb.session.stateless.RoomRateSessionBeanLocal;
import ejb.session.stateless.RoomSessionBeanLocal;
import ejb.session.stateless.RoomTypeSessionBeanLocal;
import entity.EmployeeEntity;
import entity.NormalRateEntity;
import entity.PublishedRateEntity;
import entity.RoomEntity;
import entity.RoomRateEntity;
import entity.RoomTypeEntity;
import java.math.BigDecimal;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Startup;
import util.enumeration.EmployeeAccessRightEnum;
import util.enumeration.RoomStatusEnum;
import util.exception.EmployeeNotFoundException;
import util.exception.EmployeeUsernameExistException;
import util.exception.InputDataValidationException;
import util.exception.ReservationRoomNotFoundException;
import util.exception.RoomNumberExistException;
import util.exception.RoomNotFoundException;
import util.exception.RoomRateNameExistException;
import util.exception.RoomTypeNameExistException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomTypeException;

@Singleton
@LocalBean
@Startup

public class DataInitializationSessionBean {

    @EJB
    private ReservationRoomSessionBeanLocal reservationRoomSessionBean;

    @EJB
    private RoomSessionBeanLocal roomSessionBean;

    @EJB
    private RoomRateSessionBeanLocal roomRateSessionBean;

    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBean;

    @EJB
    private EmployeeSessionBeanLocal employeeSessionBean;

    public DataInitializationSessionBean() {
    }

    @PostConstruct
    public void postConstruct() {
        try {
            employeeSessionBean.retrieveEmployeeByUsername("sysadmin");
            roomTypeSessionBean.retrieveRoomTypeByRoomTypeName("Deluxe Room");
            roomSessionBean.retrieveRoomByRoomNumber("0101");
            //roomRateSessionBean.retrieveRoomRateByRoomRateName("Deluxe Room Published");
        } catch (RoomNotFoundException | EmployeeNotFoundException | RoomTypeNotFoundException ex) {
            initializeData();
        }
    }
    
    //@Schedule(hour = "*", minute = "*", second = "*/1", info = "roomAllocationTimer")
    @Schedule(dayOfWeek = "*", hour = "2", info = "roomAllocationTimer")
    public void roomAllocationTimer() {
        System.out.println("Timer!");
        try {
            reservationRoomSessionBean.allocateRooms();
            reservationRoomSessionBean.allocateRoomExceptionType1();
            reservationRoomSessionBean.allocateRoomExceptionType2();
        } catch (ReservationRoomNotFoundException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void initializeData() {
        try {
            employeeSessionBean.createNewEmployee(new EmployeeEntity("sysadmin", "password", EmployeeAccessRightEnum.SYSTEM_ADMINISTRATOR));
            employeeSessionBean.createNewEmployee(new EmployeeEntity("opmanager", "password", EmployeeAccessRightEnum.OPERATION_MANAGER));
            employeeSessionBean.createNewEmployee(new EmployeeEntity("salesmanager", "password", EmployeeAccessRightEnum.SALES_MANAGER));
            employeeSessionBean.createNewEmployee(new EmployeeEntity("guestrelo", "password", EmployeeAccessRightEnum.GUEST_RELATION_OFFICER));

            //roomType
            RoomTypeEntity deluxeRoomEntity = roomTypeSessionBean.createNewRoomType(new RoomTypeEntity("Deluxe Room", "Premier Room"));
            RoomTypeEntity premierRoomEntity = roomTypeSessionBean.createNewRoomType(new RoomTypeEntity("Premier Room", "Family Room"));
            RoomTypeEntity familyRoomEntity = roomTypeSessionBean.createNewRoomType(new RoomTypeEntity("Family Room", "Junior Suite"));
            RoomTypeEntity juniorSuiteEntity = roomTypeSessionBean.createNewRoomType(new RoomTypeEntity("Junior Suite", "Grand Suite"));
            RoomTypeEntity grandSuiteEntity = roomTypeSessionBean.createNewRoomType(new RoomTypeEntity("Grand Suite", "None"));

            //roomRate
            roomRateSessionBean.createNewRoomRate(new PublishedRateEntity("Deluxe Room Published", new BigDecimal("100")), deluxeRoomEntity.getRoomTypeName());
            roomRateSessionBean.createNewRoomRate(new NormalRateEntity("Deluxe Room Normal", new BigDecimal("50")), deluxeRoomEntity.getRoomTypeName());
            roomRateSessionBean.createNewRoomRate(new PublishedRateEntity("Premier Room Published", new BigDecimal("200")), premierRoomEntity.getRoomTypeName());
            roomRateSessionBean.createNewRoomRate(new NormalRateEntity("Premier Room Normal", new BigDecimal("100")), premierRoomEntity.getRoomTypeName());
            roomRateSessionBean.createNewRoomRate(new PublishedRateEntity("Family Room Published", new BigDecimal("300")), familyRoomEntity.getRoomTypeName());
            roomRateSessionBean.createNewRoomRate(new NormalRateEntity("Family Room Normal", new BigDecimal("150")), familyRoomEntity.getRoomTypeName());
            roomRateSessionBean.createNewRoomRate(new PublishedRateEntity("Junior Suite Published", new BigDecimal("400")), juniorSuiteEntity.getRoomTypeName());
            roomRateSessionBean.createNewRoomRate(new NormalRateEntity("Junior Suite Normal", new BigDecimal("200")), juniorSuiteEntity.getRoomTypeName());
            roomRateSessionBean.createNewRoomRate(new PublishedRateEntity("Grand Suite Published", new BigDecimal("500")), grandSuiteEntity.getRoomTypeName());
            roomRateSessionBean.createNewRoomRate(new NormalRateEntity("Grand Suite Normal", new BigDecimal("250")), grandSuiteEntity.getRoomTypeName());
//            roomRateSessionBean.createNewRoomRate(new RoomRateEntity("Test", new BigDecimal("1")), deluxeRoomEntity);
            
            //room
            roomSessionBean.createNewRoom(new RoomEntity("0101", RoomStatusEnum.AVAILABLE), "Deluxe Room");
            roomSessionBean.createNewRoom(new RoomEntity("0201", RoomStatusEnum.AVAILABLE), "Deluxe Room");
            roomSessionBean.createNewRoom(new RoomEntity("0301", RoomStatusEnum.AVAILABLE), "Deluxe Room");
            roomSessionBean.createNewRoom(new RoomEntity("0401", RoomStatusEnum.AVAILABLE), "Deluxe Room");
            roomSessionBean.createNewRoom(new RoomEntity("0501", RoomStatusEnum.AVAILABLE), "Deluxe Room");
            roomSessionBean.createNewRoom(new RoomEntity("0102", RoomStatusEnum.AVAILABLE), "Premier Room");
            roomSessionBean.createNewRoom(new RoomEntity("0202", RoomStatusEnum.AVAILABLE), "Premier Room");
            roomSessionBean.createNewRoom(new RoomEntity("0302", RoomStatusEnum.AVAILABLE), "Premier Room");
            roomSessionBean.createNewRoom(new RoomEntity("0402", RoomStatusEnum.AVAILABLE), "Premier Room");
            roomSessionBean.createNewRoom(new RoomEntity("0502", RoomStatusEnum.AVAILABLE), "Premier Room");
            roomSessionBean.createNewRoom(new RoomEntity("0103", RoomStatusEnum.AVAILABLE), "Family Room");
            roomSessionBean.createNewRoom(new RoomEntity("0203", RoomStatusEnum.AVAILABLE), "Family Room");
            roomSessionBean.createNewRoom(new RoomEntity("0303", RoomStatusEnum.AVAILABLE), "Family Room");
            roomSessionBean.createNewRoom(new RoomEntity("0403", RoomStatusEnum.AVAILABLE), "Family Room");
            roomSessionBean.createNewRoom(new RoomEntity("0503", RoomStatusEnum.AVAILABLE), "Family Room");
            roomSessionBean.createNewRoom(new RoomEntity("0104", RoomStatusEnum.AVAILABLE), "Junior Suite");
            roomSessionBean.createNewRoom(new RoomEntity("0204", RoomStatusEnum.AVAILABLE), "Junior Suite");
            roomSessionBean.createNewRoom(new RoomEntity("0304", RoomStatusEnum.AVAILABLE), "Junior Suite");
            roomSessionBean.createNewRoom(new RoomEntity("0404", RoomStatusEnum.AVAILABLE), "Junior Suite");
            roomSessionBean.createNewRoom(new RoomEntity("0504", RoomStatusEnum.AVAILABLE), "Junior Suite");
            roomSessionBean.createNewRoom(new RoomEntity("0105", RoomStatusEnum.AVAILABLE), "Grand Suite");
            roomSessionBean.createNewRoom(new RoomEntity("0205", RoomStatusEnum.AVAILABLE), "Grand Suite");
            roomSessionBean.createNewRoom(new RoomEntity("0305", RoomStatusEnum.AVAILABLE), "Grand Suite");
            roomSessionBean.createNewRoom(new RoomEntity("0405", RoomStatusEnum.AVAILABLE), "Grand Suite");
            roomSessionBean.createNewRoom(new RoomEntity("0505", RoomStatusEnum.AVAILABLE), "Grand Suite");
            
        } catch (EmployeeUsernameExistException | UnknownPersistenceException | InputDataValidationException
                | RoomNumberExistException | RoomTypeNotFoundException | RoomTypeNameExistException | UpdateRoomTypeException | RoomRateNameExistException ex) {
            ex.printStackTrace();
        }
    }
}
