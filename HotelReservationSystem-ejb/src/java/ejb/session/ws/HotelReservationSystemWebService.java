/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.PartnerEmployeeSessionBeanLocal;
import ejb.session.stateless.ReservationSessionBeanLocal;
import ejb.session.stateless.RoomRateSessionBeanLocal;
import ejb.session.stateless.RoomTypeSessionBeanLocal;
import entity.NightEntity;
import entity.PartnerEmployeeEntity;
import entity.PeakRateEntity;
import entity.PromotionRateEntity;
import entity.ReservationEntity;
import entity.ReservationRoomEntity;
import entity.RoomRateEntity;
import entity.RoomTypeEntity;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.ReservationTypeEnum;
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

    @EJB
    private RoomRateSessionBeanLocal roomRateSessionBean;

    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBean;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    @EJB
    private ReservationSessionBeanLocal reservationSessionBeanLocal;

    @EJB
    private PartnerEmployeeSessionBeanLocal partnerEmployeeSessionBeanLocal;

    /**
     * This is a sample web service operation
     */
    
    @WebMethod(operationName = "partnerEmployeeLogin")
    public PartnerEmployeeEntity partnerEmployeeLogin(@WebParam(name = "username") String username,
                                                        @WebParam(name = "password") String password) throws InvalidLoginCredentialException {
        return partnerEmployeeSessionBeanLocal.partnerEmployeeLogin(username, password);
    }
    
    //need detach ??? 
    @WebMethod(operationName = "viewPartnerEmployeeReservations")
    public void viewAllPartnerEmployeeReservations(@WebParam(name = "username") String username) throws PartnerNotFoundException {
        PartnerEmployeeEntity partner = partnerEmployeeSessionBeanLocal.retrieveAllPartnerReservations(username);
        System.out.printf("%30s%30s%30s%30s%30s%30s%30s\n", "Reservation ID", "Guest Name", "Reservation Room Type", "Number of Rooms", "Start Date", "End Date", "Reservation Fee");
        for(ReservationEntity reservation: partner.getReservations()) {
            System.out.printf("%30s%30s%30s%30s%30s%30s%30s\n", reservation.getReservationId(), reservation.getGuest().getName(), reservation.getRoomType().getRoomTypeName(), reservation.getNumberOfRooms(), reservation.getStartDate().toString(), reservation.getEndDate().toString(), NumberFormat.getCurrencyInstance().format(reservation.getReservationFee()));
        }
    }
    
    @WebMethod(operationName = "viewPartnerEmployeeReservationDetails")
    public void viewPartnerEmployeeReservationDetails(@WebParam(name = "reservationId") Long reservationId) throws ReservationNotFoundException {
        ReservationEntity reservation = reservationSessionBeanLocal.retrieveReservationByReservationId(reservationId);
        System.out.printf("%30s%30s%30s%30s%30s%30s\n", "", "Guest Name", "Reservation Room Type", "Number of Rooms", "Start Date", "End Date", "Reservation Fee");
        System.out.printf("%30s%30s%30s%30s%30s%30s\n", reservation.getGuest().getName(), reservation.getRoomType().getRoomTypeName(), reservation.getNumberOfRooms(), reservation.getStartDate().toString(), reservation.getEndDate().toString(), NumberFormat.getCurrencyInstance().format(reservation.getReservationFee()));
    }
    
    @WebMethod(operationName = "partnerSearchRoom")
    public void partnerSearchRoom(@WebParam(name = "checkIn") String checkIn, @WebParam(name = "checkOut") String checkOut, @WebParam(name = "numberOfRooms") int numberOfRooms) throws RoomTypeNotFoundException {
        LocalDate startDate = LocalDate.parse(checkIn);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        Date checkInDate = reservationSessionBeanLocal.convertToDateViaSqlTimestamp(startDateTime.withHour(14)); //checkindate
        LocalDate endDate = LocalDate.parse(checkOut);
        LocalDateTime endDateTime = endDate.atStartOfDay();
        Date checkOutDate = reservationSessionBeanLocal.convertToDateViaSqlTimestamp(endDateTime.withHour(12)); //checkoudate
        List<RoomTypeEntity> listOfRoomTypes = roomTypeSessionBean.retrieveAllRoomTypes();
        System.out.println("Available Room Types: ");
        System.out.println();
        System.out.printf("%30s%20s%25s\n", "Room Type Name", "Rooms Available", "Reservation Amount");
        for (RoomTypeEntity roomType : listOfRoomTypes) {
            if (roomType.getIsDisabled() == false) {
                Integer totalNoOfRoomsForRoomType = roomTypeSessionBean.retrieveTotalQuantityOfRoomsBasedOnRoomType(roomType.getRoomTypeName());
                Integer reservedRoomsForRoomTypeForDateRange = roomTypeSessionBean.retrieveQuantityOfRoomsReserved(checkInDate, checkOutDate, roomType.getRoomTypeName());
                Integer remainingAvailableRooms = totalNoOfRoomsForRoomType - reservedRoomsForRoomTypeForDateRange;
                if (remainingAvailableRooms >= numberOfRooms) {
                    RoomTypeEntity roomTypeEntity = roomTypeSessionBean.retrieveRoomTypeByRoomTypeName(roomType.getRoomTypeName());
                    List<Date> dateRange = roomTypeSessionBean.getListOfDaysBetweenTwoDates(checkInDate, checkOutDate);
                    LinkedHashMap<Date, RoomRateEntity> map = new LinkedHashMap<Date, RoomRateEntity>();
                    for (Date date : dateRange) {
                        RoomRateEntity rate = null;
                        PeakRateEntity peakRate;
                        try {
                            peakRate = roomRateSessionBean.retrievePeakRateByRoomTypeAndValidityPeriod(roomTypeEntity.getRoomTypeId(), date);
                        } catch (RoomRateNotFoundException ex) {
                            peakRate = null;
                        }

                        PromotionRateEntity promotionRate;
                        try {
                            promotionRate = roomRateSessionBean.retrievePromotionRateByRoomTypeAndValidityPeriod(roomTypeEntity.getRoomTypeId(), date);
                        } catch (RoomRateNotFoundException ex) {
                            promotionRate = null;
                        }

                        if (peakRate == null & promotionRate == null) {
                            try {
                                rate = roomRateSessionBean.retrieveNormalRateByRoomType(roomTypeEntity.getRoomTypeId());
                            } catch (RoomRateNotFoundException ex) {
                                System.out.println("Error: " + ex.getMessage());
                            }
                        } else if (peakRate == null && promotionRate != null) {
                            rate = promotionRate;
                        } else if (peakRate != null && promotionRate == null) {
                            rate = peakRate;
                        } else if (peakRate != null && promotionRate != null) {
                            rate = promotionRate;
                        }

                        map.put(date, rate);
                    }

                    BigDecimal reservationAmount = new BigDecimal(0);

                    for (RoomRateEntity roomRate : map.values()) {
                        reservationAmount = reservationAmount.add(roomRate.getRatePerNight());
                    }
                    reservationAmount = reservationAmount.multiply(new BigDecimal(numberOfRooms));
                    System.out.printf("%30s%20s%25s\n", roomType.getRoomTypeName(), remainingAvailableRooms, NumberFormat.getCurrencyInstance().format(reservationAmount));
                }
            }
        }
    }
    
    @WebMethod(operationName = "partnerReserveRoom")
    public void partnerReserveRoom(@WebParam(name = "checkIn") String checkIn, @WebParam(name = "checkOut") String checkOut, @WebParam(name = "numberOfRooms") int numberOfRooms, @WebParam(name = "roomTypeOption") String roomTypeOption) throws RoomTypeNotFoundException {
        LocalDate startDate = LocalDate.parse(checkIn);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        Date checkInDate = reservationSessionBeanLocal.convertToDateViaSqlTimestamp(startDateTime.withHour(14)); //checkindate
        LocalDate endDate = LocalDate.parse(checkOut);
        LocalDateTime endDateTime = endDate.atStartOfDay();
        Date checkOutDate = reservationSessionBeanLocal.convertToDateViaSqlTimestamp(endDateTime.withHour(12));
        Long numberOfNights = (ChronoUnit.DAYS.between(checkInDate.toInstant(), checkOutDate.toInstant())) + 1;
        RoomTypeEntity roomTypeEntity = roomTypeSessionBean.retrieveRoomTypeByRoomTypeName(roomTypeOption);
        List<Date> dateRange = roomTypeSessionBean.getListOfDaysBetweenTwoDates(checkInDate, checkOutDate);
        LinkedHashMap<Date, RoomRateEntity> map = new LinkedHashMap<Date, RoomRateEntity>();
        for (Date date : dateRange) {
            RoomRateEntity rate = null;

            PeakRateEntity peakRate;
            try {
                peakRate = roomRateSessionBean.retrievePeakRateByRoomTypeAndValidityPeriod(roomTypeEntity.getRoomTypeId(), date);
                } catch (RoomRateNotFoundException ex) {
                    peakRate = null;
                }

                PromotionRateEntity promotionRate;
                try {
                    promotionRate = roomRateSessionBean.retrievePromotionRateByRoomTypeAndValidityPeriod(roomTypeEntity.getRoomTypeId(), date);
                } catch (RoomRateNotFoundException ex) {
                    promotionRate = null;
                }

                if (peakRate == null & promotionRate == null) {
                    try {
                        rate = roomRateSessionBean.retrieveNormalRateByRoomType(roomTypeEntity.getRoomTypeId());
                    } catch (RoomRateNotFoundException ex) {
                        System.out.println("Error: " + ex.getMessage());
                    }
                } else if (peakRate == null && promotionRate != null) {
                    rate = promotionRate;
                } else if (peakRate != null && promotionRate == null) {
                    rate = peakRate;
                } else if (peakRate != null && promotionRate != null) {
                    rate = promotionRate;
                }

                map.put(date, rate);
            }
            BigDecimal reservationAmount = new BigDecimal(0);

            for (RoomRateEntity roomRate : map.values()) {
                reservationAmount = reservationAmount.add(roomRate.getRatePerNight());
            }
            reservationAmount = reservationAmount.multiply(new BigDecimal(numberOfRooms));

            System.out.println("Reservation amount: $" + reservationAmount + " for " + numberOfRooms + " rooms" + " for " + numberOfNights + " nights!" + "(" + roomTypeEntity.getRoomTypeName() + ")");
            System.out.print("Would you like to make payment and confirm reservation? (Enter 'Y' to confirm) > ");
            String input = scanner.nextLine().trim();

            if (input.equals("Y")) {
                ReservationEntity newReservation = new ReservationEntity(numberOfRooms, reservationAmount, checkInDate, checkOutDate, ReservationTypeEnum.ONLINE);
                newReservation.setRoomType(roomTypeEntity);
                for (int i = 0; i < numberOfRooms; i++) {
                    ReservationRoomEntity reservationRoom = new ReservationRoomEntity();
                    reservationRoom.setReservation(newReservation);
                    newReservation.getReservationRooms().add(reservationRoom);
                }

                //add each night with their corresponding date and rate
                for (Map.Entry<Date, RoomRateEntity> entry : map.entrySet()) {
                    Date key = entry.getKey();
                    RoomRateEntity value = entry.getValue();
//          System.out.println("<KEY-VALUE> : " + key + " - " + value.getRatePerNight());
                    NightEntity night = new NightEntity(value, key);
                    try {
                        NightEntity createdNight = nightSessionBean.createNewNight(night, night.getRoomRate().getName()); //might need to account if the creation of reservation feel then roll back if not got extra nights
                        newReservation.getNights().add(createdNight);
                    } catch (InputDataValidationException | RoomRateNotFoundException | UnknownPersistenceException ex) {
                        System.out.println("Error: " + ex.getMessage());
                    }
                }
                    ReservationEntity createdReservation = reservationSessionBean.createNewReservation(currentCustomerEntity.getGuestId(), newReservation);
                    System.out.println("Reservation created successfully!  [Reservation ID: " + createdReservation.getReservationId() + "]\n");

            } else {
                System.out.println("No reservation made!\n");
            }

    }
}
