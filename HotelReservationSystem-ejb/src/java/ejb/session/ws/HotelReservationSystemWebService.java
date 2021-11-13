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
import entity.RoomEntity;
import entity.RoomRateEntity;
import entity.RoomTypeEntity;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.GuestEmailExistException;
import util.exception.GuestNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.PartnerNotFoundException;
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
        for (RoomTypeEntity roomType : listOfRoomTypes) {
////        
            em.detach(roomType);

            for (RoomEntity room : roomType.getRooms()) {
                em.detach(room);
//                    room.setRoomType(null);
            }

            for (RoomRateEntity rate : roomType.getRoomRates()) {
                em.detach(rate);
//                    rate.setRoomType(null);
            }

            roomType.getRooms().clear();
            roomType.getRoomRates().clear();

        }
        return listOfRoomTypes;
    }

    @WebMethod(operationName = "retrieveTotalQuantityOfRoomsBasedOnRoomType")
    public Integer retrieveTotalQuantityOfRoomsBasedOnRoomType(@WebParam(name = "roomTypeInput") String roomTypeInput) {
        return roomTypeSessionBeanLocal.retrieveTotalQuantityOfRoomsBasedOnRoomType(roomTypeInput);
    }

    @WebMethod(operationName = "retrieveQuantityOfRoomsReserved")
    public Integer retrieveQuantityOfRoomsReserved(@WebParam(name = "start") String start, @WebParam(name = "end") String end, @WebParam(name = "roomTypeName") String roomTypeName) {
//        LocalDate startDate = LocalDate.parse(start);
//        LocalDateTime startDateTime = startDate.atStartOfDay();
//        Date checkInDate = convertToDateViaSqlTimestamp(startDateTime.withHour(14)); //checkindate
//        LocalDate endDate = LocalDate.parse(end);
//        LocalDateTime endDateTime = endDate.atStartOfDay();
//        Date checkOutDate = convertToDateViaSqlTimestamp(endDateTime.withHour(12)); //checkoudate

//        System.out.println("***********" + roomTypeName + "***********");
//        System.out.println("***********test**************" + start + "**************************");
//        System.out.println("***********123****************" + convertStringToDate(start) + "**************************");
//        System.out.println("***********abc****************" + convertStringToDate(end) + "**************************");
//        System.out.println("***********def*****************" + end + "**************************");
//        return roomTypeSessionBeanLocal.retrieveQuantityOfRoomsReserved(checkInDate, checkOutDate, roomTypeName);
        return roomTypeSessionBeanLocal.retrieveQuantityOfRoomsReserved(convertStringToDate(start), convertStringToDate(end), roomTypeName);
    }

    @WebMethod(operationName = "retrieveRoomTypeByRoomTypeName")
    public RoomTypeEntity retrieveRoomTypeByRoomTypeName(@WebParam(name = "roomTypeName") String roomTypeName) throws RoomTypeNotFoundException {
        RoomTypeEntity roomType = roomTypeSessionBeanLocal.retrieveRoomTypeByRoomTypeName(roomTypeName);

        em.detach(roomType);

        for (RoomEntity room : roomType.getRooms()) {
            em.detach(room);
//                    room.setRoomType(null);
        }

        for (RoomRateEntity rate : roomType.getRoomRates()) {
            em.detach(rate);
//                    rate.setRoomType(null);
        }

        roomType.getRooms().clear();
        roomType.getRoomRates().clear();

        return roomType;

    }

    @WebMethod(operationName = "retrievePeakRateByRoomTypeAndValidityPeriod")
    public PeakRateEntity retrievePeakRateByRoomTypeAndValidityPeriod(@WebParam(name = "roomTypeId") Long roomTypeId, @WebParam(name = "dateOf") String dateOf) throws RoomRateNotFoundException {
//        LocalDate startDate = LocalDate.parse(dateOf);
//        LocalDateTime startDateTime = startDate.atStartOfDay();
//        Date date = convertToDateViaSqlTimestamp(startDateTime.withHour(14)); //checkindate

        PeakRateEntity rate = roomRateSessionBeanLocal.retrievePeakRateByRoomTypeAndValidityPeriod(roomTypeId, convertStringToDate(dateOf));
        em.detach(rate);
        em.detach(rate.getRoomType());
        rate.setRoomType((null));
        for (NightEntity night : rate.getNights()) {
            em.detach(night);
        }

        rate.getNights().clear();
        return rate;

    }

    @WebMethod(operationName = "retrievePromotionRateByRoomTypeAndValidityPeriod")
    public PromotionRateEntity retrievePromotionRateByRoomTypeAndValidityPeriod(@WebParam(name = "roomTypeId") Long roomTypeId, @WebParam(name = "dateOf") String dateOf) throws RoomRateNotFoundException {
//        LocalDate startDate = LocalDate.parse(dateOf);
//        LocalDateTime startDateTime = startDate.atStartOfDay();
//        Date date = convertToDateViaSqlTimestamp(startDateTime.withHour(14)); //checkindate
        PromotionRateEntity rate = roomRateSessionBeanLocal.retrievePromotionRateByRoomTypeAndValidityPeriod(roomTypeId, convertStringToDate(dateOf));

        em.detach(rate);
        em.detach(rate.getRoomType());
        rate.setRoomType((null));
        for (NightEntity night : rate.getNights()) {
            em.detach(night);
        }

        rate.getNights().clear();
        return rate;

    }

    @WebMethod(operationName = "retrieveNormalRateByRoomType")
    public NormalRateEntity retrieveNormalRateByRoomType(@WebParam(name = "roomTypeId") Long roomTypeId) throws RoomRateNotFoundException {
        NormalRateEntity rate = roomRateSessionBeanLocal.retrieveNormalRateByRoomType(roomTypeId);

        em.detach(rate);
        em.detach(rate.getRoomType());
        rate.setRoomType((null));
        for (NightEntity night : rate.getNights()) {
            em.detach(night);
        }

        rate.getNights().clear();
        return rate;
    }

    @WebMethod(operationName = "retrieveGuestByEmail")
    public GuestEntity retrieveGuestByEmail(@WebParam(name = "email") String email) throws GuestNotFoundException {

        GuestEntity guest = guestSessionBeanLocal.retrieveGuestByEmail(email);

        em.detach(guest);

        for (ReservationEntity reservation : guest.getReservations()) {
            em.detach(reservation);
        }

        guest.getReservations().clear();

        return guest;

    }

    @WebMethod(operationName = "createNewGuest")
    public Long createNewGuest(@WebParam(name = "createGuestRecord") GuestEntity createGuestRecord) throws GuestEmailExistException, UnknownPersistenceException, InputDataValidationException {

        return guestSessionBeanLocal.createNewGuest(createGuestRecord);

//        em.detach(guest);
//
//        for (ReservationEntity reservation : guest.getReservations()) {
//            em.detach(reservation);
//        }
//
//        guest.getReservations().clear();
//
//        return guest;
    }

    @WebMethod(operationName = "createNewNight")
    public NightEntity createNewNight(@WebParam(name = "newNightEntity") NightEntity newNightEntity, @WebParam(name = "roomRateName") String roomRateName) throws UnknownPersistenceException, InputDataValidationException, RoomRateNotFoundException {

        NightEntity night = nightSessionBeanLocal.createNewNight(newNightEntity, roomRateName);

        em.detach(night);
        em.detach(night.getRoomRate());
        night.setRoomRate((null));

        return night;
    }

    @WebMethod(operationName = "createNewReservation")
    public ReservationEntity createNewReservation(@WebParam(name = "guestId") Long guestId, @WebParam(name = "newReservationEntity") ReservationEntity newReservationEntity) throws GuestNotFoundException, InputDataValidationException, UnknownPersistenceException {

        ReservationEntity reservation = reservationSessionBeanLocal.createNewReservation(guestId, newReservationEntity);

//        em.detach(reservation);
//        em.detach(reservation.getGuest());
//        reservation.setGuest(null);
//
//        for (ReservationRoomEntity reservationRoom : reservation.getReservationRooms()) {
//            em.detach(reservationRoom);
//            reservationRoom.setReservation(null);
//            em.detach(reservationRoom.getRoom());
//            reservationRoom.setRoom(null);
//            em.detach(reservationRoom.getExceptionRecord());
//            reservationRoom.setExceptionRecord(null);
//        }
//
//        reservation.getReservationRooms().clear();
        

        em.detach(reservation);
        em.detach(reservation.getGuest());
        reservation.setGuest(null);
        
        for (ReservationEntity reservationByGuest : reservation.getGuest().getReservations()) {
            em.detach(reservationByGuest);
        }
        reservation.getGuest().getReservations().clear();
        
        em.detach(reservation);
        em.detach(reservation.getRoomType());
        reservation.setRoomType(null);
        
        for (NightEntity night : reservation.getNights()) {
            em.detach(night);
            em.detach(night.getRoomRate());
            night.setRoomRate(null);
        }
        reservation.getNights().clear();

        for (ReservationRoomEntity reservationRoom : reservation.getReservationRooms()) {
            em.detach(reservationRoom);
            em.detach(reservationRoom.getReservation());
            reservationRoom.setReservation(null);
            em.detach(reservationRoom.getRoom());
            reservationRoom.setRoom(null);
            em.detach(reservationRoom.getRoom().getRoomType());
            reservationRoom.getRoom().setRoomType(null);
            em.detach(reservationRoom.getExceptionRecord());
            reservationRoom.setExceptionRecord(null);
        }
        reservation.getReservationRooms().clear();

        return reservation;
    }
    
    
    //        for (NightEntity night : reservation.getNights()) {
//            em.detach(night);
//        }
//        reservation.getNights().clear();
//        em.detach(reservation);
//        em.detach(reservation.getGuest());
//        reservation.setGuest(null);
//
//        for (ReservationEntity reservationEntity : reservation.getGuest().getReservations()) {
//            em.detach(reservationEntity);
//            em.detach(reservationEntity.getGuest());
//            reservationEntity.setGuest(null);
//        }
//        reservation.getGuest().getReservations().clear();
//
//        em.detach(reservation.getRoomType());
//        reservation.setRoomType(null);
//
//        for (ReservationRoomEntity reservationRoom : reservation.getReservationRooms()) {
//            em.detach(reservationRoom);
//            reservationRoom.setReservation(null);
//        }
//
//        reservation.getReservationRooms().clear();
//
//        for (NightEntity night : reservation.getNights()) {
//            em.detach(night);
//        }
//
//        reservation.getNights().clear();
    
    

    public Date convertToDateViaSqlTimestamp(LocalDateTime dateToConvert) {
        return java.sql.Timestamp.valueOf(dateToConvert);
    }

    public Date convertStringToDate(String strDate) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        try {
            Date date = dateFormat.parse(strDate);
            return date;
        } catch (ParseException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        return new Date();
    }

}
