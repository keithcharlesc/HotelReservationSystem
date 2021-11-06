/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.RoomRateSessionBeanLocal;
import ejb.session.stateless.RoomSessionBeanLocal;
import ejb.session.stateless.RoomTypeSessionBeanLocal;
import entity.EmployeeEntity;
import entity.RoomEntity;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import util.enumeration.EmployeeAccessRightEnum;
import util.enumeration.RoomStatusEnum;
import util.exception.EmployeeNotFoundException;
import util.exception.EmployeeUsernameExistException;
import util.exception.InputDataValidationException;
import util.exception.RoomNameExistException;
import util.exception.RoomNotFoundException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author xianhui
 */
@Singleton
@LocalBean
@Startup

public class DataInitializationSessionBean {

    @EJB
    private RoomSessionBeanLocal roomSessionBean;

    @EJB
    private RoomRateSessionBeanLocal roomRateSessionBean;

    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBean;

    @EJB
    private EmployeeSessionBeanLocal employeeSessionBean;

    
    
    
    public DataInitializationSessionBean()
    {
    }
    
    
    
    @PostConstruct
    public void postConstruct()
    {
        try
        {
            employeeSessionBean.retrieveEmployeeByUsername("sysadmin");
            roomTypeSessionBean.retrieveRoomTypeByRoomTypeName("Deluxe Room");
            roomSessionBean.retrieveRoomByRoomNumber(0101);
            //roomRateSessionBean.retrieveRoomRateByRoomRateName("Deluxe Room Published");
        }
        catch(RoomNotFoundException |  EmployeeNotFoundException | RoomTypeNotFoundException ex)
        {
            initializeData();
        }
    }
        
    
    
        
    
        
    
        
    

    private void initializeData()
    {
        try
        {
            employeeSessionBean.createNewEmployee(new EmployeeEntity("sysadmin", "password", EmployeeAccessRightEnum.SYSTEM_ADMINISTRATOR));
            employeeSessionBean.createNewEmployee(new EmployeeEntity("opmanager", "password", EmployeeAccessRightEnum.OPERATION_MANAGER));
            employeeSessionBean.createNewEmployee(new EmployeeEntity("salesmanager", "password", EmployeeAccessRightEnum.SALES_MANAGER));
            employeeSessionBean.createNewEmployee(new EmployeeEntity("guestrelo", "password", EmployeeAccessRightEnum.GUEST_RELATION_OFFICER));
            
            //roomType
            
            //roomRate
            //roomRateSessionBean.createNewRoomRate( new PublishedRateEntity("Deluxe Room Published", 100 (change to string?), no start and end date  
            
            roomSessionBean.createNewRoom(new RoomEntity(0101, RoomStatusEnum.AVAILABLE),  "Deluxe Room");
            roomSessionBean.createNewRoom(new RoomEntity(0201, RoomStatusEnum.AVAILABLE),  "Deluxe Room");
            roomSessionBean.createNewRoom(new RoomEntity(0301, RoomStatusEnum.AVAILABLE),  "Deluxe Room");
            roomSessionBean.createNewRoom(new RoomEntity(0401, RoomStatusEnum.AVAILABLE),  "Deluxe Room");
            roomSessionBean.createNewRoom(new RoomEntity(0501, RoomStatusEnum.AVAILABLE),  "Deluxe Room");
            roomSessionBean.createNewRoom(new RoomEntity(0102, RoomStatusEnum.AVAILABLE),  "Premier Room");
            roomSessionBean.createNewRoom(new RoomEntity(0202, RoomStatusEnum.AVAILABLE),  "Premier Room");
            roomSessionBean.createNewRoom(new RoomEntity(0302, RoomStatusEnum.AVAILABLE),  "Premier Room");
            roomSessionBean.createNewRoom(new RoomEntity(0402, RoomStatusEnum.AVAILABLE),  "Premier Room");
            roomSessionBean.createNewRoom(new RoomEntity(0502, RoomStatusEnum.AVAILABLE),  "Premier Room");
            roomSessionBean.createNewRoom(new RoomEntity(0103, RoomStatusEnum.AVAILABLE),  "Family Room");
            roomSessionBean.createNewRoom(new RoomEntity(0203, RoomStatusEnum.AVAILABLE),  "Family Room");
            roomSessionBean.createNewRoom(new RoomEntity(0303, RoomStatusEnum.AVAILABLE),  "Family Room");
            roomSessionBean.createNewRoom(new RoomEntity(0403, RoomStatusEnum.AVAILABLE),  "Family Room");
            roomSessionBean.createNewRoom(new RoomEntity(0503, RoomStatusEnum.AVAILABLE),  "Family Room");
            roomSessionBean.createNewRoom(new RoomEntity(0104, RoomStatusEnum.AVAILABLE),  "Junior Suite");
            roomSessionBean.createNewRoom(new RoomEntity(0204, RoomStatusEnum.AVAILABLE),  "Junior Suite");
            roomSessionBean.createNewRoom(new RoomEntity(0304, RoomStatusEnum.AVAILABLE),  "Junior Suite");
            roomSessionBean.createNewRoom(new RoomEntity(0404, RoomStatusEnum.AVAILABLE),  "Junior Suite");
            roomSessionBean.createNewRoom(new RoomEntity(0504, RoomStatusEnum.AVAILABLE),  "Junior Suite");
            roomSessionBean.createNewRoom(new RoomEntity(0105, RoomStatusEnum.AVAILABLE),  "Grand Suite");
            roomSessionBean.createNewRoom(new RoomEntity(0205, RoomStatusEnum.AVAILABLE),  "Grand Suite");
            roomSessionBean.createNewRoom(new RoomEntity(0305, RoomStatusEnum.AVAILABLE),  "Grand Suite");
            roomSessionBean.createNewRoom(new RoomEntity(0405, RoomStatusEnum.AVAILABLE),  "Grand Suite");
            roomSessionBean.createNewRoom(new RoomEntity(0505, RoomStatusEnum.AVAILABLE),  "Grand Suite");
        }
        catch(EmployeeUsernameExistException | UnknownPersistenceException | InputDataValidationException| RoomNameExistException | RoomTypeNotFoundException ex)
        {
            ex.printStackTrace();
        }
    }
}
