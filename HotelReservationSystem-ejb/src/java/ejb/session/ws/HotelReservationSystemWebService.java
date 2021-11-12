/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.GuestSessionBeanLocal;
import ejb.session.stateless.NightSessionBeanLocal;
import ejb.session.stateless.PartnerEmployeeSessionBeanLocal;
import ejb.session.stateless.ReservationSessionBeanLocal;
import ejb.session.stateless.RoomRateSessionBeanLocal;
import ejb.session.stateless.RoomTypeSessionBeanLocal;
import entity.GuestEntity;
import entity.NightEntity;
import entity.NormalRateEntity;
import entity.PartnerEmployeeEntity;
import entity.PeakRateEntity;
import entity.PromotionRateEntity;
import entity.ReservationEntity;
import entity.ReservationRoomEntity;
import entity.RoomRateEntity;
import entity.RoomTypeEntity;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import util.enumeration.ReservationTypeEnum;
import util.exception.GuestEmailExistException;
import util.exception.GuestNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.PartnerNotFoundException;
import util.exception.ReservationNotFoundException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author keithcharleschan
 */
@WebService(serviceName = "HotelReservationSystemWebService")
@Stateless()
public class HotelReservationSystemWebService {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @EJB
    private RoomRateSessionBeanLocal roomRateSessionBeanLocal;

    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBeanLocal;

    @EJB
    private NightSessionBeanLocal nightSessionBeanLocal;

    @EJB
    private ReservationSessionBeanLocal reservationSessionBeanLocal;

    @EJB
    private PartnerEmployeeSessionBeanLocal partnerEmployeeSessionBeanLocal;

    @EJB
    private GuestSessionBeanLocal guestSessionBeanLocal;

    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "partnerEmployeeLogin")
    public PartnerEmployeeEntity partnerEmployeeLogin(@WebParam(name = "username") String username,
            @WebParam(name = "password") String password)
            throws InvalidLoginCredentialException {
        return partnerEmployeeSessionBeanLocal.partnerEmployeeLogin(username, password);
    }

//    //need detach ??? 
    @WebMethod(operationName = "viewPartnerEmployeeReservations")
    public List<ReservationEntity> viewAllPartnerEmployeeReservations(@WebParam(name = "username") String username) throws PartnerNotFoundException {
        PartnerEmployeeEntity partner = partnerEmployeeSessionBeanLocal.retrieveAllPartnerReservations(username);
//        System.out.printf("%30s%30s%30s%30s%30s%30s%30s\n", "Reservation ID", "Guest Name", "Reservation Room Type", "Number of Rooms", "Start Date", "End Date", "Reservation Fee");
//        for (ReservationEntity reservation : partner.getReservations()) {
//            System.out.printf("%30s%30s%30s%30s%30s%30s%30s\n", reservation.getReservationId(), reservation.getGuest().getName(), reservation.getRoomType().getRoomTypeName(), reservation.getNumberOfRooms(), reservation.getStartDate().toString(), reservation.getEndDate().toString(), NumberFormat.getCurrencyInstance().format(reservation.getReservationFee()));
//        }
        List<ReservationEntity> reservations = partner.getReservations();
        for (ReservationEntity reservation : reservations) {
            em.detach(reservation);
            em.detach(reservation.getGuest());
            reservation.getGuest().getReservations().clear();
        }

        return reservations;
    }

    @WebMethod(operationName = "retrieveAllRoomTypes")
    public List<RoomTypeEntity> retrieveAllRoomTypes() {
        List<RoomTypeEntity> listOfRoomTypes = roomTypeSessionBeanLocal.retrieveAllRoomTypes();
//        
//        for (RoomTypeEntity roomType : listOfRoomTypes) {
//////            em.detach(roomType);
//            em.detach(roomType.getRooms().size());
//            roomType.getRooms().clear();
//            em.detach(roomType.getRoomRates().size());
//            roomType.getRoomRates().clear();
//              
//
//        }
        
        return listOfRoomTypes;
    }

    @WebMethod(operationName = "retrieveTotalQuantityOfRoomsBasedOnRoomType")
    public Integer retrieveTotalQuantityOfRoomsBasedOnRoomType(@WebParam(name = "roomTypeInput") String roomTypeInput) {
        return roomTypeSessionBeanLocal.retrieveTotalQuantityOfRoomsBasedOnRoomType(roomTypeInput);
    }

    @WebMethod(operationName = "retrieveQuantityOfRoomsReserved")
    public Integer retrieveQuantityOfRoomsReserved(@WebParam(name = "checkInDate") XMLGregorianCalendar checkInDate, @WebParam(name = "checkOutDate") XMLGregorianCalendar checkOutDate, @WebParam(name = "roomTypeName") String roomTypeName) {
        return roomTypeSessionBeanLocal.retrieveQuantityOfRoomsReserved(date(checkInDate), date(checkOutDate), roomTypeName);
    }

    @WebMethod(operationName = "retrieveRoomTypeByRoomTypeName")
    public RoomTypeEntity retrieveRoomTypeByRoomTypeName(@WebParam(name = "roomTypeName") String roomTypeName) throws RoomTypeNotFoundException {
        return roomTypeSessionBeanLocal.retrieveRoomTypeByRoomTypeName(roomTypeName);
    }

    @WebMethod(operationName = "retrievePeakRateByRoomTypeAndValidityPeriod")
    public PeakRateEntity retrievePeakRateByRoomTypeAndValidityPeriod(@WebParam(name = "roomTypeId") Long roomTypeId, @WebParam(name = "dateOf") XMLGregorianCalendar dateOf) throws RoomRateNotFoundException {
        return roomRateSessionBeanLocal.retrievePeakRateByRoomTypeAndValidityPeriod(roomTypeId, date(dateOf));
    }

    @WebMethod(operationName = "retrievePromotionRateByRoomTypeAndValidityPeriod")
    public PromotionRateEntity retrievePromotionRateByRoomTypeAndValidityPeriod(@WebParam(name = "roomTypeId") Long roomTypeId, @WebParam(name = "dateOf") XMLGregorianCalendar dateOf) throws RoomRateNotFoundException {
        return roomRateSessionBeanLocal.retrievePromotionRateByRoomTypeAndValidityPeriod(roomTypeId, date(dateOf));
    }

    @WebMethod(operationName = "retrieveNormalRateByRoomType")
    public NormalRateEntity retrieveNormalRateByRoomType(@WebParam(name = "roomTypeId") Long roomTypeId) throws RoomRateNotFoundException {
        return roomRateSessionBeanLocal.retrieveNormalRateByRoomType(roomTypeId);
    }

//    public Date convertToDateViaSqlTimestamp(LocalDateTime dateToConvert) {
//        return java.sql.Timestamp.valueOf(dateToConvert);
//    }
//
//    public static long numberOfNights(Date firstDate, Date secondDate) throws IOException {
//        return ChronoUnit.DAYS.between(firstDate.toInstant(), secondDate.toInstant());
//    }
//
//    private List<Date> getListOfDaysBetweenTwoDates(Date startDate, Date endDate) {
//        List<Date> result = new ArrayList<Date>();
//        Calendar start = Calendar.getInstance();
//        start.setTime(startDate);
//        Calendar end = Calendar.getInstance();
//        end.setTime(endDate);
////        end.add(Calendar.DAY_OF_YEAR, 1); //Add 1 day to endDate to make sure endDate is included into the final list
//        while (start.before(end)) {
//            result.add(start.getTime());
//            start.add(Calendar.DAY_OF_YEAR, 1);
//        }
//        return result;
//    }

    private String formatDate(XMLGregorianCalendar xmlGregorianCalendarInstance) {
        return date(xmlGregorianCalendarInstance).toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString();
    }

    private static java.util.Date date(XMLGregorianCalendar xmlGregorianCalendarInstance) {
        return xmlGregorianCalendarInstance.toGregorianCalendar().getTime();
    }

    private static XMLGregorianCalendar xml(java.util.Date date) {
        try {
            GregorianCalendar gcalendar = new GregorianCalendar();
            gcalendar.setTime(date);
            XMLGregorianCalendar xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcalendar);
            return xmlDate;
        } catch (DatatypeConfigurationException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

}
