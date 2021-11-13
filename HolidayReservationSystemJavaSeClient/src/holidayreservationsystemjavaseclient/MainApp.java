/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationsystemjavaseclient;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import java.util.Scanner;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import ws.client.GuestEmailExistException_Exception;
import ws.client.GuestEntity;
import ws.client.GuestNotFoundException_Exception;
import ws.client.HotelReservationSystemWebService_Service;
import ws.client.InputDataValidationException_Exception;
import ws.client.InvalidLoginCredentialException_Exception;
import ws.client.NightEntity;
import ws.client.PartnerEmployeeEntity;
import ws.client.PeakRateEntity;
import ws.client.PromotionRateEntity;
import ws.client.ReservationEntity;
import ws.client.ReservationRoomEntity;
import ws.client.ReservationTypeEnum;
import ws.client.RoomRateEntity;
import ws.client.RoomRateNotFoundException_Exception;
import ws.client.RoomTypeEntity;
import ws.client.RoomTypeNotFoundException_Exception;
import ws.client.UnknownPersistenceException_Exception;

/**
 *
 * @author keithcharleschan
 */
public class MainApp {

    HotelReservationSystemWebService_Service service = new HotelReservationSystemWebService_Service();
    private PartnerEmployeeEntity currentPartnerEmployeeEntity;

    public MainApp() {

    }

    public void runApp() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        if (currentPartnerEmployeeEntity != null) {
            registeredPartnerEmployeeMenu();
        } else {
            while (true) {
                System.out.println("*** Welcome to Hotel Reservation (HoR) System ***\n");
                System.out.println("1: Login");
                System.out.println("2: Search Hotel Room");
                System.out.println("3: Exit\n");
                response = 0;

                while (response < 1 || response > 3) {
                    System.out.print("> ");

                    response = scanner.nextInt();

                    if (response == 1) {
                        doLogin();
                        System.out.println("Login successful!\n");
                        registeredPartnerEmployeeMenu();
                    } else if (response == 2) {
//                        doSearchHotelRoom();
                    } else if (response == 3) {
                        break;
                    } else {
                        System.out.println("Invalid option, please try again!\n");
                    }
                }

                if (response == 3) {
                    break;
                }
            }
        }
    }

    private void doLogin() {
        Scanner scanner = new Scanner(System.in);
        String username = "";
        String password = "";

        System.out.println("*** HoRS System :: Guest Login ***\n");
        System.out.print("Enter username> ");
        username = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();

        if (username.length() > 0 && password.length() > 0) {
            try {
                currentPartnerEmployeeEntity = service.getHotelReservationSystemWebServicePort().partnerEmployeeLogin(username, password);
            } catch (InvalidLoginCredentialException_Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    private void registeredPartnerEmployeeMenu() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        System.out.println("Welcome! You are logged in as " + currentPartnerEmployeeEntity.getName());

        while (true) {
            System.out.println("*** Holiday.com - Hotel Reservation (HoR) System ***\n");
            System.out.println("1: Search Hotel Room"); //includes reserve hotel room
            System.out.println("2: View Reservation Details");
            System.out.println("3: View All Reservations");
            System.out.println("4: Logout\n");
            response = 0;

            while (response < 1 || response > 4) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    doPartnerSearchHotelRoom();
                } else if (response == 2) {
//                    viewReservationDetails();
                } else if (response == 3) {
//                    viewAllReservations();
                } else if (response == 4) {
                    currentPartnerEmployeeEntity = null;
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 4) {
                break;
            }
        }
    }

    public void doPartnerSearchHotelRoom() {
        Scanner scanner = new Scanner(System.in);
        String startInput = "";
        String endInput = "";
        System.out.println("*** HoRS System :: Front Office :: Walk-In Search Room ***\n");
        System.out.print("Enter Check-In Date [yyyy-MM-dd] > ");
        startInput = scanner.nextLine().trim();
        LocalDate startDate = LocalDate.parse(startInput);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        Date checkInDate = convertToDateViaSqlTimestamp(startDateTime.withHour(14)); //checkindate
        System.out.print("Enter Check-Out Date [yyyy-MM-dd] > ");
        endInput = scanner.nextLine();
        LocalDate endDate = LocalDate.parse(endInput);
        LocalDateTime endDateTime = endDate.atStartOfDay();
        Date checkOutDate = convertToDateViaSqlTimestamp(endDateTime.withHour(12)); //checkoudate
        System.out.println();

        System.out.print("Number of rooms > ");
        Integer numberOfRooms = scanner.nextInt();
        scanner.nextLine();
        System.out.println();
        //call list of room types that have available rooms
//            System.out.println(roomTypeSessionBean.retrieveTotalQuantityOfRoomsBasedOnRoomType("Deluxe Room")); //total quantity of rooms per room type
        List<RoomTypeEntity> listOfRoomTypes = service.getHotelReservationSystemWebServicePort().retrieveAllRoomTypes();
        System.out.println("Available Room Types: ");
        System.out.println();
        System.out.printf("%30s%20s%25s\n", "Room Type Name", "Rooms Available", "Reservation Amount");
        for (RoomTypeEntity roomType : listOfRoomTypes) {
//            if (roomType.getIsDisabled() == false) {
            Integer totalNoOfRoomsForRoomType = service.getHotelReservationSystemWebServicePort().retrieveTotalQuantityOfRoomsBasedOnRoomType(roomType.getRoomTypeName());
//            System.out.println(convertDateToString(checkInDate));
//            System.out.println(convertDateToString(checkOutDate));
//            
            String start = convertDateToString(checkInDate);
            String end = convertDateToString(checkOutDate);
//            System.out.println(start);
//            System.out.println(end);

            Integer reservedRoomsForRoomTypeForDateRange = service.getHotelReservationSystemWebServicePort().retrieveQuantityOfRoomsReserved(start, end, roomType.getRoomTypeName());
            Integer remainingAvailableRooms = totalNoOfRoomsForRoomType - reservedRoomsForRoomTypeForDateRange;
//if (10 >= numberOfRooms) {      
            if (remainingAvailableRooms >= numberOfRooms) {

                try {
                    RoomTypeEntity roomTypeEntity = service.getHotelReservationSystemWebServicePort().retrieveRoomTypeByRoomTypeName(roomType.getRoomTypeName());
                    List<Date> dateRange = getListOfDaysBetweenTwoDates(checkInDate, checkOutDate);
                    LinkedHashMap<Date, RoomRateEntity> map = new LinkedHashMap<Date, RoomRateEntity>();
                    for (Date date : dateRange) {
                        RoomRateEntity rate = null;

                        PeakRateEntity peakRate;
                        try {
                            peakRate = service.getHotelReservationSystemWebServicePort().retrievePeakRateByRoomTypeAndValidityPeriod(roomTypeEntity.getRoomTypeId(), convertDateToString(date));
                        } catch (RoomRateNotFoundException_Exception ex) {
                            peakRate = null;
                        }

                        PromotionRateEntity promotionRate;
                        try {
                            promotionRate = service.getHotelReservationSystemWebServicePort().retrievePromotionRateByRoomTypeAndValidityPeriod(roomTypeEntity.getRoomTypeId(), convertDateToString(date));
                        } catch (RoomRateNotFoundException_Exception ex) {
                            promotionRate = null;
                        }

                        if (peakRate == null & promotionRate == null) {
                            try {
                                rate = service.getHotelReservationSystemWebServicePort().retrieveNormalRateByRoomType(roomTypeEntity.getRoomTypeId());
                            } catch (RoomRateNotFoundException_Exception ex) {
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
//                     System.out.printf("%30s%20s%25s\n", roomType.getRoomTypeName(), 10, NumberFormat.getCurrencyInstance().format(reservationAmount));
                } catch (RoomTypeNotFoundException_Exception ex) {
                    System.out.println("Error: " + ex.getMessage());
                }
            }
//            }
        }

        System.out.println();
        System.out.print("Would the Walk-in Guest like to make a reservation? (Enter 'Y' to confirm) > ");
        String input = scanner.nextLine().trim();

        if (input.equals("Y")) {
            doWalkInReserveRoom(numberOfRooms, checkInDate, checkOutDate);
        } else {
            System.out.println("No reservation made!\n");
        }

    }

    public void doWalkInReserveRoom(Integer numberOfRooms, Date checkInDate, Date checkOutDate) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Indicate Room Type Option> ");
        String roomTypeOption = scanner.nextLine().trim();

        try {
            Long numberOfNights = (numberOfNights(checkInDate, checkOutDate)) + 1;
            RoomTypeEntity roomTypeEntity = service.getHotelReservationSystemWebServicePort().retrieveRoomTypeByRoomTypeName(roomTypeOption);
            List<Date> dateRange = getListOfDaysBetweenTwoDates(checkInDate, checkOutDate);
            LinkedHashMap<Date, RoomRateEntity> map = new LinkedHashMap<Date, RoomRateEntity>();
            for (Date date : dateRange) {
                RoomRateEntity rate = null;

                PeakRateEntity peakRate;
                try {
                    peakRate = service.getHotelReservationSystemWebServicePort().retrievePeakRateByRoomTypeAndValidityPeriod(roomTypeEntity.getRoomTypeId(), convertDateToString(date));
                } catch (RoomRateNotFoundException_Exception ex) {
                    peakRate = null;
                }

                PromotionRateEntity promotionRate;
                try {
                    promotionRate = service.getHotelReservationSystemWebServicePort().retrievePromotionRateByRoomTypeAndValidityPeriod(roomTypeEntity.getRoomTypeId(), convertDateToString(date));
                } catch (RoomRateNotFoundException_Exception ex) {
                    promotionRate = null;
                }

                if (peakRate == null & promotionRate == null) {
                    try {
                        rate = service.getHotelReservationSystemWebServicePort().retrieveNormalRateByRoomType(roomTypeEntity.getRoomTypeId());
                    } catch (RoomRateNotFoundException_Exception ex) {
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
            System.out.print("Would the Walk-in Guest like to make payment and confirm reservation? (Enter 'Y' to confirm) > ");
            String input = scanner.nextLine().trim();

            if (input.equals("Y")) {

                System.out.print("Guest Name > ");
                String guestName = scanner.nextLine().trim();
                System.out.print("Guest Email > ");
                String guestEmail = scanner.nextLine().trim();
                System.out.print("Guest Phone Number > ");
                String guestPhoneNumber = scanner.nextLine().trim();

                try {
                    GuestEntity hasGuestRecord = service.getHotelReservationSystemWebServicePort().retrieveGuestByEmail(guestEmail);
                    System.out.println("123");
                } catch (GuestNotFoundException_Exception ex) {
                    GuestEntity createGuestRecord = new GuestEntity();
                    createGuestRecord.setName(guestName);
                    createGuestRecord.setEmail(guestEmail);
                    createGuestRecord.setPhoneNumber(guestPhoneNumber);
                    try {
                        service.getHotelReservationSystemWebServicePort().createNewGuest(createGuestRecord);
                        System.out.println("456");
                    } catch (GuestEmailExistException_Exception | InputDataValidationException_Exception | UnknownPersistenceException_Exception exception) {
                        System.out.println("Error: " + exception.getMessage());
                    }
                }

                try {
                    GuestEntity guest = service.getHotelReservationSystemWebServicePort().retrieveGuestByEmail(guestEmail);
                    System.out.println("789");
                    ReservationEntity newReservation = new ReservationEntity();
                    newReservation.setNumberOfRooms(numberOfRooms);
                    newReservation.setReservationFee(reservationAmount);
                    newReservation.setStartDate(xml(checkInDate));
                    newReservation.setEndDate(xml(checkOutDate));
                    newReservation.setReservationType(ReservationTypeEnum.WALK_IN);

//                 ReservationEntity newReservation = new ReservationEntity(numberOfRooms, reservationAmount, checkInDate, checkOutDate, ReservationTypeEnum.WALK_IN);
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
                        NightEntity night = new NightEntity();
                        night.setDate(xml(key)); //GEORGIA DATE
                        night.setRoomRate(value);
                        try {
                            NightEntity createdNight = service.getHotelReservationSystemWebServicePort().createNewNight(night, night.getRoomRate().getName()); //might need to account if the creation of reservation feel then roll back if not got extra nights
                            System.out.println("101112");
                            newReservation.getNights().add(createdNight);
                        } catch (InputDataValidationException_Exception | RoomRateNotFoundException_Exception | UnknownPersistenceException_Exception ex) {
                            System.out.println("Error: " + ex.getMessage());
                        }
                    }

                    try {
                        System.out.println("Woah");
                        ReservationEntity createdReservation = service.getHotelReservationSystemWebServicePort().createNewReservation(guest.getGuestId(), newReservation);
                        System.out.println("131415");
                        System.out.println("Reservation created successfully!  [Reservation ID: " + createdReservation.getReservationId() + "]\n");
                    } catch (GuestNotFoundException_Exception | InputDataValidationException_Exception | UnknownPersistenceException_Exception ex) {
                        System.out.println("An error has occurred: " + ex.getMessage() + "\n");;
                    }
                } catch (GuestNotFoundException_Exception ex) {
                    System.out.println("Error: " + ex.getMessage());
                }

            } else {
                System.out.println("No reservation made!\n");
            }
        } catch (RoomTypeNotFoundException_Exception | IOException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    public Date convertToDateViaSqlTimestamp(LocalDateTime dateToConvert) {
        return java.sql.Timestamp.valueOf(dateToConvert);
    }

    public static long numberOfNights(Date firstDate, Date secondDate) throws IOException {
        return ChronoUnit.DAYS.between(firstDate.toInstant(), secondDate.toInstant());
    }

    private List<Date> getListOfDaysBetweenTwoDates(Date startDate, Date endDate) {
        List<Date> result = new ArrayList<Date>();
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);
//        end.add(Calendar.DAY_OF_YEAR, 1); //Add 1 day to endDate to make sure endDate is included into the final list
        while (start.before(end)) {
            result.add(start.getTime());
            start.add(Calendar.DAY_OF_YEAR, 1);
        }
        return result;
    }

    public String convertDateToString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        String strDate = dateFormat.format(date);
        return strDate;
    }

    public Date convertStringToDate(String strDate) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
            Date date = dateFormat.parse(strDate);
            return date;
        } catch (ParseException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        return new Date();
    }

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
            return null;
        }
    }

}
