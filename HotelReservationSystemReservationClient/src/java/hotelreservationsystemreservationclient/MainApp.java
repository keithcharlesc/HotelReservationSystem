/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotelreservationsystemreservationclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.ExceptionRecordSessionBeanRemote;
import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.NightSessionBeanRemote;
import ejb.session.stateless.PartnerEmployeeSessionBeanRemote;
import ejb.session.stateless.ReservationRoomSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.CustomerEntity;
import entity.NightEntity;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import util.enumeration.ReservationTypeEnum;
import util.exception.GuestEmailExistException;
import util.exception.GuestNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.ReservationNotFoundException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author keithcharleschan
 */
public class MainApp {

    private ReservationSessionBeanRemote reservationSessionBean;
    private RoomRateSessionBeanRemote roomRateSessionBean;
    private RoomSessionBeanRemote roomSessionBean;
    private RoomTypeSessionBeanRemote roomTypeSessionBean;
    private ReservationRoomSessionBeanRemote reservationRoomSessionBean;
    private NightSessionBeanRemote nightSessionBean;
    private ExceptionRecordSessionBeanRemote exceptionRecordSessionBean;
    private EmployeeSessionBeanRemote employeeSessionBean;
    private PartnerEmployeeSessionBeanRemote partnerEmployeeSessionBean;
    private GuestSessionBeanRemote guestSessionBean;

    private CustomerEntity currentCustomerEntity;

    public MainApp() {
    }

    public MainApp(ReservationSessionBeanRemote reservationSessionBean, RoomRateSessionBeanRemote roomRateSessionBean, RoomSessionBeanRemote roomSessionBean, RoomTypeSessionBeanRemote roomTypeSessionBean, ReservationRoomSessionBeanRemote reservationRoomSessionBean, NightSessionBeanRemote nightSessionBean, ExceptionRecordSessionBeanRemote exceptionRecordSessionBean, EmployeeSessionBeanRemote employeeSessionBean, PartnerEmployeeSessionBeanRemote partnerEmployeeSessionBean, GuestSessionBeanRemote guestSessionBean) {
        this.reservationSessionBean = reservationSessionBean;
        this.roomRateSessionBean = roomRateSessionBean;
        this.roomSessionBean = roomSessionBean;
        this.roomTypeSessionBean = roomTypeSessionBean;
        this.reservationRoomSessionBean = reservationRoomSessionBean;
        this.nightSessionBean = nightSessionBean;
        this.exceptionRecordSessionBean = exceptionRecordSessionBean;
        this.employeeSessionBean = employeeSessionBean;
        this.partnerEmployeeSessionBean = partnerEmployeeSessionBean;
        this.guestSessionBean = guestSessionBean;
        this.currentCustomerEntity = null;
    }

    public void runApp() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        if (currentCustomerEntity != null) {
            registeredCustomerMenu();
        } else {
            while (true) {
                System.out.println("*** Welcome to Hotel Reservation (HoR) System ***\n");
                System.out.println("1: Login");
                System.out.println("2: Register as Guest");
                System.out.println("3: Search Hotel Room");
                System.out.println("4: Exit\n");
                response = 0;

                while (response < 1 || response > 4) {
                    System.out.print("> ");

                    response = scanner.nextInt();

                    if (response == 1) {
                        try {
                            doLogin();
                            System.out.println("Login successful!\n");
                            registeredCustomerMenu();
                        } catch (InvalidLoginCredentialException ex) {
                            System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                        }
                    } else if (response == 2) {
                        try {
                            doRegister();
//                            currentCustomerEntity = (CustomerEntity) guestSessionBean.retrieveGuestByGuestId(customerId);
//                            registeredCustomerMenu();
                        } catch (GuestEmailExistException | UnknownPersistenceException | InputDataValidationException ex) {
                            System.out.println("Error: " + ex.getMessage());
                        }
                    } else if (response == 3) {
                        doSearchHotelRoom();
                    } else if (response == 4) {
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
    }

    private void doLogin() throws InvalidLoginCredentialException {
        Scanner scanner = new Scanner(System.in);
        String email = "";
        String password = "";

        System.out.println("*** HoRS System :: Guest Login ***\n");
        System.out.print("Enter email> ");
        email = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();

        if (email.length() > 0 && password.length() > 0) {
            currentCustomerEntity = guestSessionBean.guestLogin(email, password);
        } else {
            throw new InvalidLoginCredentialException("Missing login credential!");
        }
    }

    private void doRegister() throws GuestEmailExistException, UnknownPersistenceException, InputDataValidationException {
        Scanner scanner = new Scanner(System.in);
        String name = "";
        String email = "";
        String phoneNo = "";
        String password = "";

        System.out.println("*** HoRS Reservation Client :: Register as Guest ***\n");
        System.out.print("Enter email> ");
        email = scanner.nextLine().trim();
        System.out.print("Enter name> ");
        name = scanner.nextLine().trim();
        System.out.print("Enter phoneNo> ");
        phoneNo = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();
        guestSessionBean.createNewGuest(new CustomerEntity(name, email, phoneNo, password));
        System.out.println("Account created successfully! Please go to login page to login.");
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();

    }

    private void registeredCustomerMenu() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        System.out.println("Welcome! You are logged in as " + currentCustomerEntity.getName());

        while (true) {
            System.out.println("*** Hotel Reservation (HoR) System ***\n");
            System.out.println("1: Search Hotel Room"); //includes reserve hotel room
            System.out.println("2: View My Reservation Details");
            System.out.println("3: View All My Reservations");
            System.out.println("4: Logout\n");
            response = 0;

            while (response < 1 || response > 4) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    doGuestSearchHotelRoom();
//                    int nextResponse = 0;
//                    while (true) {
//                        System.out.println("*** Hotel Reservation (HoR) System ***\n");
//                        System.out.println("1: Reserve Hotel Room");
//                        System.out.println("2: Back");
//                        while (nextResponse < 1 || nextResponse > 2) {
//                            System.out.print("> ");
//
//                            response = scanner.nextInt();
//
//                            if (nextResponse == 1) {
//                                //reserveHotelRoom();
//                            } else if (nextResponse == 2) {
//                                break;
//                            } else {
//                                System.out.println("Invalid option, please try again!\n");
//                            }
//                        }
//                        if (nextResponse == 2) {
//                            break;
//                        }
//                    }
                } else if (response == 2) {
                    viewReservationDetails();
                } else if (response == 3) {
                    viewAllReservations();
                } else if (response == 4) {
                    currentCustomerEntity = null;
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

    public void doGuestSearchHotelRoom() {
        Scanner scanner = new Scanner(System.in);
        String start = "";
        String end = "";
        System.out.println("*** HoRS System :: Reservation Client :: Hotel Search Room ***\n");
        System.out.print("Enter Check-In Date [yyyy-MM-dd] > ");
        start = scanner.nextLine().trim();
        LocalDate startDate = LocalDate.parse(start);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        Date checkInDate = convertToDateViaSqlTimestamp(startDateTime.withHour(14)); //checkindate
        System.out.print("Enter Check-Out Date [yyyy-MM-dd] > ");
        end = scanner.nextLine();
        LocalDate endDate = LocalDate.parse(end);
        LocalDateTime endDateTime = endDate.atStartOfDay();
        Date checkOutDate = convertToDateViaSqlTimestamp(endDateTime.withHour(12)); //checkoudate
        System.out.println();

        System.out.print("Number of rooms > ");
        Integer numberOfRooms = scanner.nextInt();
        scanner.nextLine();
        System.out.println();
        //call list of room types that have available rooms
//            System.out.println(roomTypeSessionBean.retrieveTotalQuantityOfRoomsBasedOnRoomType("Deluxe Room")); //total quantity of rooms per room type
        List<RoomTypeEntity> listOfRoomTypes = roomTypeSessionBean.retrieveAllRoomTypes();
        System.out.println("Available Room Types: ");
        System.out.println();
        System.out.printf("%30s%20s%25s\n", "Room Type Name", "Rooms Available", "Reservation Amount");
        for (RoomTypeEntity roomType : listOfRoomTypes) {
            if (roomType.getIsDisabled() == false) {
                Integer totalNoOfRoomsForRoomType = roomTypeSessionBean.retrieveTotalQuantityOfRoomsBasedOnRoomType(roomType.getRoomTypeName());
//                System.out.println("*Total Number of Rooms For " + roomType.getRoomTypeName() + ", is " + totalNoOfRoomsForRoomType);
                Integer reservedRoomsForRoomTypeForDateRange = roomTypeSessionBean.retrieveQuantityOfRoomsReserved(checkInDate, checkOutDate, roomType.getRoomTypeName());
//                System.out.println("Reserved Rooms For " + roomType.getRoomTypeName() + ", is " + reservedRoomsForRoomTypeForDateRange);
                Integer remainingAvailableRooms = totalNoOfRoomsForRoomType - reservedRoomsForRoomTypeForDateRange;
//                System.out.println("Remaining Avail Rooms For " + roomType.getRoomTypeName() + ", is " + remainingAvailableRooms);
                if (remainingAvailableRooms >= numberOfRooms) {
                    //if there is sufficient rooms available
                    //display the room type

                    try {
                        RoomTypeEntity roomTypeEntity = roomTypeSessionBean.retrieveRoomTypeByRoomTypeName(roomType.getRoomTypeName());
                        List<Date> dateRange = getListOfDaysBetweenTwoDates(checkInDate, checkOutDate);
                        LinkedHashMap<Date, RoomRateEntity> map = new LinkedHashMap<Date, RoomRateEntity>();
                        for (Date date : dateRange) {
                            RoomRateEntity rate = null;
                            //for each date,
                            //find the available rates (where the validity period contains the date) >= date<=
                            //check for normal
                            //check for peak
                            //check for promotion
                            //if promotion
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
                            //if peak or promotion exists (one only)
                            //- takes precedence over normal rate
                            // if both peak and promotion exists
                            //-take the rate that is lower
                        }

                        //PRINT RATE AMOUNTS for WHATS AVAIL
                        BigDecimal reservationAmount = new BigDecimal(0);

                        for (RoomRateEntity roomRate : map.values()) {
                            reservationAmount = reservationAmount.add(roomRate.getRatePerNight());
                        }
                        reservationAmount = reservationAmount.multiply(new BigDecimal(numberOfRooms));
                        System.out.printf("%30s%20s%25s\n", roomType.getRoomTypeName(), remainingAvailableRooms, NumberFormat.getCurrencyInstance().format(reservationAmount));
//                System.out.println("Reservation amount: $" + reservationAmount + " for " + numberOfRooms + " rooms" + " for " + numberOfNights + " nights!");

                    } catch (RoomTypeNotFoundException ex) {
                        System.out.println("Error: " + ex.getMessage());
                    }
                }
            }
        }

        System.out.println();
        System.out.print("Would you like to make a reservation? (Enter 'Y' to confirm) > ");
        String input = scanner.nextLine().trim();

        if (input.equals("Y")) {
//                                 doGuestReserveHotelRoom(reservationAmount, numberOfRooms, checkInDate, checkOutDate, roomTypeEntity, map);
            doGuestReserveHotelRoom(numberOfRooms, checkInDate, checkOutDate);
        } else {
            System.out.println("No reservation made!\n");
        }

    }

    public void doGuestReserveHotelRoom(Integer numberOfRooms, Date checkInDate, Date checkOutDate) {

        Scanner scanner = new Scanner(System.in);
        System.out.print("Indicate Room Type Option> ");
        String roomTypeOption = scanner.nextLine().trim();

        try {
            Long numberOfNights = (numberOfNights(checkInDate, checkOutDate)) + 1;
            RoomTypeEntity roomTypeEntity = roomTypeSessionBean.retrieveRoomTypeByRoomTypeName(roomTypeOption);
            List<Date> dateRange = getListOfDaysBetweenTwoDates(checkInDate, checkOutDate);
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

                try {
                    ReservationEntity createdReservation = reservationSessionBean.createNewReservation(currentCustomerEntity.getGuestId(), newReservation);
                    System.out.println("Reservation created successfully!  [Reservation ID: " + createdReservation.getReservationId() + "]\n");
                } catch (GuestNotFoundException | InputDataValidationException | UnknownPersistenceException ex) {
                    System.out.println("An error has occurred: " + ex.getMessage() + "\n");;
                }

            } else {
                System.out.println("No reservation made!\n");
            }

        } catch (RoomTypeNotFoundException | IOException ex) {
            System.out.println("Error: " + ex.getMessage());
        }

    }

    public void viewAllReservations() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** Hotel Reservation (HoR) System :: View All Reservations ***\n");
        try {
            List<ReservationEntity> reservations = guestSessionBean.retrieveGuestByGuestId(currentCustomerEntity.getGuestId()).getReservations();
            System.out.printf("%20s%20s%30s%20s%30s%30s\n", "Reservation ID", "Number of Rooms", "Room Type", "Reservation Fee", "Start Date", "End Date");
            for (ReservationEntity reservation : reservations) {
                System.out.printf("%20s%20s%30s%20s%30s%30s\n", reservation.getReservationId(), reservation.getNumberOfRooms(), reservation.getRoomType().getRoomTypeName(), NumberFormat.getCurrencyInstance().format(reservation.getReservationFee()), reservation.getStartDate(), reservation.getEndDate());
            }
        } catch (GuestNotFoundException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        System.out.println();
        System.out.print("Press any key to continue...> ");
        sc.nextLine();
    }

    public void viewReservationDetails() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** Hotel Reservation (HoR) System :: View Reservation Details ***\n");
        System.out.print("Enter Reservation ID > ");
        Long reservationId = sc.nextLong();
        sc.nextLine();
        try {
            ReservationEntity reservation = reservationSessionBean.retrieveReservationByReservationId(reservationId);
            System.out.printf("%20s%20s%30s%20s%30s%30s\n", "Reservation ID", "Number of Rooms", "Room Type", "Reservation Fee", "Start Date", "End Date");
            System.out.printf("%20s%20s%30s%20s%30s%30s\n", reservation.getReservationId(), reservation.getNumberOfRooms(), reservation.getRoomType().getRoomTypeName(), NumberFormat.getCurrencyInstance().format(reservation.getReservationFee()), reservation.getStartDate(), reservation.getEndDate());
        } catch (ReservationNotFoundException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        System.out.println();
        System.out.print("Press any key to continue...> ");
        sc.nextLine();
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

    public void doSearchHotelRoom() {
        Scanner scanner = new Scanner(System.in);
        String start = "";
        String end = "";
        System.out.println("*** HoRS Reservation Client :: Search Hotel Room ***\n");
        System.out.print("Enter Check-In Date [yyyy-MM-dd] > ");
        start = scanner.nextLine().trim();
        LocalDate startDate = LocalDate.parse(start);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        Date checkInDate = convertToDateViaSqlTimestamp(startDateTime.withHour(14)); //checkindate
        System.out.print("Enter Check-Out Date [yyyy-MM-dd] > ");
        end = scanner.nextLine();
        LocalDate endDate = LocalDate.parse(end);
        LocalDateTime endDateTime = endDate.atStartOfDay();
        Date checkOutDate = convertToDateViaSqlTimestamp(endDateTime.withHour(12)); //checkoudate
        System.out.println();
        try {
            Long numberOfNights = (numberOfNights(checkInDate, checkOutDate)) + 1;
            System.out.print("Number of rooms > ");
            Integer numberOfRooms = scanner.nextInt();
            scanner.nextLine();
            System.out.println();
            //call list of room types that have available rooms
//            System.out.println(roomTypeSessionBean.retrieveTotalQuantityOfRoomsBasedOnRoomType("Deluxe Room")); //total quantity of rooms per room type
            List<RoomTypeEntity> listOfRoomTypes = roomTypeSessionBean.retrieveAllRoomTypes();
            System.out.println("Available Room Types: ");
            for (RoomTypeEntity roomType : listOfRoomTypes) {
                int i = 1;

                Integer totalNoOfRoomsForRoomType = roomTypeSessionBean.retrieveTotalQuantityOfRoomsBasedOnRoomType(roomType.getRoomTypeName());
//                System.out.println("*Total Number of Rooms For " + roomType.getRoomTypeName() + ", is " + totalNoOfRoomsForRoomType);
                Integer reservedRoomsForRoomTypeForDateRange = roomTypeSessionBean.retrieveQuantityOfRoomsReserved(checkInDate, checkOutDate, end);
//                System.out.println("Reserved Rooms For " + roomType.getRoomTypeName() + ", is " + reservedRoomsForRoomTypeForDateRange);
                Integer remainingAvailableRooms = totalNoOfRoomsForRoomType - reservedRoomsForRoomTypeForDateRange;
//                System.out.println("Remaining Avail Rooms For " + roomType.getRoomTypeName() + ", is " + remainingAvailableRooms);
                if (remainingAvailableRooms >= numberOfRooms) {
                    //if there is sufficient rooms available
                    //display the room type
                    System.out.println("- " + roomType.getRoomTypeName());
                }
            }
            System.out.println();
            System.out.print("Indicate Room Type Option> ");
            String roomTypeOption = scanner.nextLine().trim();
            try {
                RoomTypeEntity roomTypeEntity = roomTypeSessionBean.retrieveRoomTypeByRoomTypeName(roomTypeOption);

                List<Date> dateRange = getListOfDaysBetweenTwoDates(checkInDate, checkOutDate);
                LinkedHashMap<Date, RoomRateEntity> map = new LinkedHashMap<Date, RoomRateEntity>();
                for (Date date : dateRange) {
                    RoomRateEntity rate = null;

                    //for each date,
                    //find the available rates (where the validity period contains the date) >= date<=
                    //check for normal
                    //check for peak
                    //check for promotion
                    //if promotion
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
                    //if peak or promotion exists (one only)
                    //- takes precedence over normal rate
                    // if both peak and promotion exists
                    //-take the rate that is lower
                }

                //PRINT RATE AMOUNTS for WHATS AVAIL
                BigDecimal reservationAmount = new BigDecimal(0);

                System.out.println();
                System.out.println("Retrieving room rates for each night...");
                System.out.println();
                System.out.printf("%30s%30s%30s\n", "Date", "Room Rate", "Rate Per Night");
                for (Map.Entry<Date, RoomRateEntity> entry : map.entrySet()) {
                    Date key = entry.getKey();
                    RoomRateEntity value = entry.getValue();
//                    System.out.println("<KEY-VALUE> : " + key + " - " + value.getRatePerNight());
                    String df = DateFormat.getDateInstance().format(key);
                    System.out.printf("%30s%30s%30s\n", df, value.getName(), NumberFormat.getCurrencyInstance().format(value.getRatePerNight()));
                }

                System.out.println();

                for (RoomRateEntity roomRate : map.values()) {
                    reservationAmount = reservationAmount.add(roomRate.getRatePerNight());
                }
                reservationAmount = reservationAmount.multiply(new BigDecimal(numberOfRooms));

//                List<RoomRateEntity> roomRates = roomTypeEntity.getRoomRates(); //jpql to get room rate = published rate
//                RoomRateEntity publishedRate = new PublishedRateEntity();
//                for (RoomRateEntity roomRate : roomRates) {
//                    if (roomRate.getClass().getSimpleName().equals("PublishedRateEntity")) {
//                        publishedRate = (PublishedRateEntity) roomRate;
//                    }
//                }
//                BigDecimal reservationAmount = publishedRate.getRatePerNight().multiply(new BigDecimal(numberOfNights)).multiply(new BigDecimal(numberOfRooms)); //convert number of nights to make it big decimal
                System.out.println("Reservation amount: $" + reservationAmount + " for " + numberOfRooms + " rooms" + " for " + numberOfNights + " nights!");
                System.out.println();
                System.out.print("Press any key to continue...> ");
                scanner.nextLine();
            } catch (RoomTypeNotFoundException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
//
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
        }

    }

}
