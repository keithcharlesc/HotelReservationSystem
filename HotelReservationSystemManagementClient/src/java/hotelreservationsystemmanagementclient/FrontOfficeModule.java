package hotelreservationsystemmanagementclient;

import ejb.session.stateless.ExceptionRecordSessionBeanRemote;
import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.NightSessionBeanRemote;
import ejb.session.stateless.ReservationRoomSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.EmployeeEntity;
import entity.ExceptionRecordEntity;
import entity.GuestEntity;
import entity.NightEntity;
import entity.ReservationEntity;
import entity.ReservationRoomEntity;
import entity.RoomEntity;
import entity.RoomRateEntity;
import entity.RoomTypeEntity;
import java.io.IOException;
import java.math.BigDecimal;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.EmployeeAccessRightEnum;
import util.enumeration.ReservationTypeEnum;
import util.exception.ExceptionRecordNotFoundException;
import util.exception.GuestEmailExistException;
import util.exception.GuestNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidAccessRightException;
import util.exception.ReservationRoomNotFoundException;
import util.exception.RoomNotFoundException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomException;

public class FrontOfficeModule {

    private RoomSessionBeanRemote roomSessionBean;
    private RoomTypeSessionBeanRemote roomTypeSessionBean;
    private GuestSessionBeanRemote guestSessionBean;
    private ReservationSessionBeanRemote reservationSessionBean;
    private NightSessionBeanRemote nightSessionBean;
    private RoomRateSessionBeanRemote roomRateSessionBean;
    private ReservationRoomSessionBeanRemote reservationRoomSessionBean;
    private ExceptionRecordSessionBeanRemote exceptionRecordSessionBean;
    private EmployeeEntity currentEmployee;
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public FrontOfficeModule() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public FrontOfficeModule(RoomSessionBeanRemote roomSessionBean, RoomTypeSessionBeanRemote roomTypeSessionBean, GuestSessionBeanRemote guestSessionBean, ReservationSessionBeanRemote reservationSessionBean, NightSessionBeanRemote nightSessionBean, RoomRateSessionBeanRemote roomRateSessionBean, ExceptionRecordSessionBeanRemote exceptionRecordSessionBean, ReservationRoomSessionBeanRemote reservationRoomSessionBean, EmployeeEntity currentEmployee) {
        this();
        this.exceptionRecordSessionBean = exceptionRecordSessionBean;
        this.roomSessionBean = roomSessionBean;
        this.roomTypeSessionBean = roomTypeSessionBean;
        this.guestSessionBean = guestSessionBean;
        this.reservationSessionBean = reservationSessionBean;
        this.nightSessionBean = nightSessionBean;
        this.roomRateSessionBean = roomRateSessionBean;
        this.reservationRoomSessionBean = reservationRoomSessionBean;
        this.currentEmployee = currentEmployee;
    }

    public void menuFrontOffice() throws InvalidAccessRightException {

        if (currentEmployee.getEmployeeAccessRightEnum() != EmployeeAccessRightEnum.GUEST_RELATION_OFFICER) {
            throw new InvalidAccessRightException("You don't have GUEST RELATION OFFICER rights to access the system administration module.");
        }

        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** HoRS System :: Guest Relation Officer Operation ***\n");
            System.out.println("1: Walk-In Search Room"); //includes walk-in reserve room
            System.out.println("2: Check-in Guest");
            System.out.println("3: Check-out Guest");
            System.out.println("4: Back\n");
            response = 0;

            while (response < 1 || response > 3) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    doWalkInSearchRoom();
                } else if (response == 2) {
                    doCheckInGuest();
                } else if (response == 3) {
                    doCheckOutGuest();
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

    public void doWalkInSearchRoom() {
        Scanner scanner = new Scanner(System.in);
        String start = "";
        String end = "";
        System.out.println("*** HoRS System :: Front Office :: Walk-In Search Room ***\n");
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
                Integer reservedRoomsForRoomTypeForDateRange = roomTypeSessionBean.retrieveQuantityOfRoomsReserved(checkInDate, checkOutDate, roomType.getRoomTypeName());
                Integer remainingAvailableRooms = totalNoOfRoomsForRoomType - reservedRoomsForRoomTypeForDateRange;
                if (remainingAvailableRooms >= numberOfRooms) {

                    try {
                        RoomTypeEntity roomTypeEntity = roomTypeSessionBean.retrieveRoomTypeByRoomTypeName(roomType.getRoomTypeName());
                        List<Date> dateRange = getListOfDaysBetweenTwoDates(checkInDate, checkOutDate);
                        LinkedHashMap<Date, RoomRateEntity> map = new LinkedHashMap<Date, RoomRateEntity>();
                        for (Date date : dateRange) {
                            RoomRateEntity rate = null;

                            try {
                                rate = roomRateSessionBean.retrievePublishedRateByRoomType(roomTypeEntity.getRoomTypeId());
                            } catch (RoomRateNotFoundException ex) {
                                System.out.println("Error: " + ex.getMessage());
                            }

                            map.put(date, rate);

                        }

                        BigDecimal reservationAmount = new BigDecimal(0);

                        for (RoomRateEntity roomRate : map.values()) {
                            reservationAmount = reservationAmount.add(roomRate.getRatePerNight());
                        }
                        reservationAmount = reservationAmount.multiply(new BigDecimal(numberOfRooms));
                        System.out.printf("%30s%20s%25s\n", roomType.getRoomTypeName(), remainingAvailableRooms, NumberFormat.getCurrencyInstance().format(reservationAmount));

                    } catch (RoomTypeNotFoundException ex) {
                        System.out.println("Error: " + ex.getMessage());
                    }
                }
            }
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

//    public void doWalkInSearchRoom() {
//        Scanner scanner = new Scanner(System.in);
//        String start = "";
//        String end = "";
//        System.out.println("*** HoRS System :: Front Office :: Walk-In Search Room ***\n");
//        System.out.print("Enter Check-In Date [yyyy-MM-dd] > ");
//        start = scanner.nextLine().trim();
//        LocalDate startDate = LocalDate.parse(start);
//        LocalDateTime startDateTime = startDate.atStartOfDay();
////        System.out.println("Before adding 2pm, " + startDateTime);
//        Date checkInDate = convertToDateViaSqlTimestamp(startDateTime.withHour(14)); //checkindate
////        System.out.println("After adding 2pm, " + checkInDate);
//        System.out.print("Enter Check-Out Date [yyyy-MM-dd] > ");
//        end = scanner.nextLine();
//        LocalDate endDate = LocalDate.parse(end);
//        LocalDateTime endDateTime = endDate.atStartOfDay();
////        System.out.println("Before adding 12pm, " + endDateTime);
//        Date checkOutDate = convertToDateViaSqlTimestamp(endDateTime.withHour(12)); //checkoudate
////        System.out.println("After adding 12pm, " + checkOutDate);
//        try {
//            Long numberOfNights = (numberOfNights(checkInDate, checkOutDate)) + 1;
//            //need to acccount for 12pm 2pm for check in and checkout to set it to
//            System.out.print("Number of rooms > ");
//            Integer numberOfRooms = scanner.nextInt();
//            scanner.nextLine();
//            System.out.println();
//            //call list of room types that have available rooms
////            System.out.println(roomTypeSessionBean.retrieveTotalQuantityOfRoomsBasedOnRoomType("Deluxe Room")); //total quantity of rooms per room type
//            List<RoomTypeEntity> listOfRoomTypes = roomTypeSessionBean.retrieveAllRoomTypes();
//            System.out.println("Available Room Types: ");
//            for (RoomTypeEntity roomType : listOfRoomTypes) {
//                int i = 1;
//
//                Integer totalNoOfRoomsForRoomType = roomTypeSessionBean.retrieveTotalQuantityOfRoomsBasedOnRoomType(roomType.getRoomTypeName());
////                System.out.println("*Total Number of Rooms For " + roomType.getRoomTypeName() + ", is " + totalNoOfRoomsForRoomType);
//                Integer reservedRoomsForRoomTypeForDateRange = roomTypeSessionBean.retrieveQuantityOfRoomsReserved(checkInDate, checkOutDate, end);
////                System.out.println("Reserved Rooms For " + roomType.getRoomTypeName() + ", is " + reservedRoomsForRoomTypeForDateRange);
//                Integer remainingAvailableRooms = totalNoOfRoomsForRoomType - reservedRoomsForRoomTypeForDateRange;
////                System.out.println("Remaining Avail Rooms For " + roomType.getRoomTypeName() + ", is " + remainingAvailableRooms);
//                if (remainingAvailableRooms >= numberOfRooms) {
//                    //if there is sufficient rooms available
//                    //display the room type
//                    System.out.println("- " + roomType.getRoomTypeName());
//                }
//            }
//            System.out.println();
//            System.out.print("Indicate Room Type Option> ");
//            String roomTypeOption = scanner.nextLine().trim();
//            System.out.println();
//            try {
//                RoomTypeEntity roomTypeEntity = roomTypeSessionBean.retrieveRoomTypeByRoomTypeName(roomTypeOption);
//                List<RoomRateEntity> roomRates = roomTypeEntity.getRoomRates(); //jpql to get room rate = published rate
//                RoomRateEntity publishedRate = new PublishedRateEntity();
//                for (RoomRateEntity roomRate : roomRates) {
//                    if (roomRate.getClass().getSimpleName().equals("PublishedRateEntity")) {
//                        publishedRate = (PublishedRateEntity)roomRate;
//                    }
//                }
//                
//                List<Date> dateRange = getListOfDaysBetweenTwoDates(checkInDate, checkOutDate);
//                System.out.printf("%30s%30s%30s\n", "Date", "Room Rate", "Rate Per Night");
//                for (Date date:dateRange) {
//                    String df = DateFormat.getDateInstance().format(date);
//                    System.out.printf("%30s%30s%30s\n", df, publishedRate.getName(), NumberFormat.getCurrencyInstance().format(publishedRate.getRatePerNight()));
//                }
//                System.out.println();
//                
//                BigDecimal reservationAmount = publishedRate.getRatePerNight().multiply(new BigDecimal(numberOfNights)).multiply(new BigDecimal(numberOfRooms)); //convert number of nights to make it big decimal
//                System.out.println("Reservation amount: $" + reservationAmount + " for " + numberOfRooms + " rooms" + " for " + numberOfNights + " nights!");
//                System.out.print("Would you like to make a reservation? (Enter 'Y' to confirm) > ");
//                String input = scanner.nextLine().trim();
//
//                if (input.equals("Y")) {
//                    doWalkInReserveRoom(reservationAmount, numberOfRooms, checkInDate, checkOutDate, publishedRate, roomTypeEntity);
//                } else {
//                    System.out.println("Reservation not made!\n");
//                }
//
//            } catch (RoomTypeNotFoundException ex) {
//                System.out.println("Error: " + ex.getMessage());
//            }
//
////            System.out.println("numberOfNights (days difference): " + numberOfNights);
//        } catch (IOException ex) {
//            System.out.println("Error: " + ex.getMessage());
//        }
//
//    }
    //to be updated 
    public void doWalkInReserveRoom(Integer numberOfRooms, Date checkInDate, Date checkOutDate) {
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

                try {
                    rate = roomRateSessionBean.retrievePublishedRateByRoomType(roomTypeEntity.getRoomTypeId());
                } catch (RoomRateNotFoundException ex) {
                    System.out.println("Error: " + ex.getMessage());
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
                    GuestEntity hasGuestRecord = guestSessionBean.retrieveGuestByEmail(guestEmail);
                } catch (GuestNotFoundException ex) {
                    GuestEntity createGuestRecord = new GuestEntity(guestName, guestEmail, guestPhoneNumber);
                    try {
                        guestSessionBean.createNewGuest(createGuestRecord);
                    } catch (GuestEmailExistException | UnknownPersistenceException | InputDataValidationException exception) {
                        System.out.println("Error: " + exception.getMessage());
                    }
                }

                try {
                    GuestEntity guest = guestSessionBean.retrieveGuestByEmail(guestEmail);
                    ReservationEntity newReservation = new ReservationEntity(numberOfRooms, reservationAmount, checkInDate, checkOutDate, ReservationTypeEnum.WALK_IN);
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
                        ReservationEntity createdReservation = reservationSessionBean.createNewReservation(guest.getGuestId(), newReservation);
                        System.out.println("Reservation created successfully!  [Reservation ID: " + createdReservation.getReservationId() + "]\n");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date current = sdf.parse(sdf.format(new Date()));
                        LocalDateTime beginning = convertToLocalDateTimeViaInstant(current).truncatedTo(ChronoUnit.HOURS);
                        Date beginningOfCurrentDate = convertToDateViaSqlTimestamp(beginning.plusHours(2));

                        LocalDateTime end = convertToLocalDateTimeViaInstant(current).truncatedTo(ChronoUnit.HOURS);
                        Date endOfCurrentDate = convertToDateViaSqlTimestamp(end.plusHours(23).plusMinutes(59).plusSeconds(59));
                        System.out.println("BEGINNING OF CURRENT: " + beginningOfCurrentDate + " END OF CURRENT: " + endOfCurrentDate + " RESERVATION DATE: " + checkInDate);
                        if (checkInDate.after(beginningOfCurrentDate) && checkInDate.before(endOfCurrentDate)) {
                            reservationRoomSessionBean.allocateRooms(checkInDate);
                            reservationRoomSessionBean.allocateRoomExceptionType1(checkInDate);
                            reservationRoomSessionBean.allocateRoomExceptionType2(checkInDate);
                        }
                        System.out.print("Press any key to continue...> ");
                        scanner.nextLine();
                    } catch (GuestNotFoundException | InputDataValidationException | UnknownPersistenceException ex) {
                        System.out.println("Error: " + ex.getMessage() + "\n");
                    } catch (ReservationRoomNotFoundException ex) {
                        System.out.println("Error: " + ex.getMessage() + "\n");
                    } catch (ParseException ex) {
                        System.out.println("Error: " + ex.getMessage() + "\n");
                    }
                } catch (GuestNotFoundException ex) {
                    System.out.println("Error: " + ex.getMessage());
                }

            } else {
                System.out.println("No reservation made!\n");
            }
        } catch (RoomTypeNotFoundException | IOException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    public void doCheckInGuest() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("*** HoRS System :: Check-In Guest ***\n");
            System.out.print("Guest Email > ");
            String guestEmail = scanner.nextLine().trim();
            GuestEntity guest = guestSessionBean.retreiveGuestReservations(guestEmail);
            System.out.printf("%30s%30s%30s%30s%30s%30s\n", "Reservation ID", "Check In Date", "Check Out Date", "Room Number", "Exception Record ID", "Type of Exception Record");
            for (ReservationEntity reservation : guest.getReservations()) {
                for (ReservationRoomEntity reservationRoom : reservation.getReservationRooms()) {
                    if (reservationRoom.getExceptionRecord() != null) {
                        if (reservationRoom.getExceptionRecord().getTypeOfException() == 1) {
                            System.out.printf("%30s%30s%30s%30s%30s%30s\n", reservation.getReservationId(), reservation.getStartDate().toString(), reservation.getEndDate().toString(), reservationRoom.getRoom().getNumber().toString(), reservationRoom.getExceptionRecord().getExceptionRecordId(), reservationRoom.getExceptionRecord().getTypeOfException());
                        } else {
                            System.out.printf("%30s%30s%30s%30s%30s%30s\n", reservation.getReservationId(), reservation.getStartDate().toString(), reservation.getEndDate().toString(), " ", reservationRoom.getExceptionRecord().getExceptionRecordId(), reservationRoom.getExceptionRecord().getTypeOfException());
                        }
                    } else {
                        //System.out.printf("%30s%30s%30s%30s\n", reservation.getStartDate().toString(), reservation.getEndDate().toString(), reservationRoom.getRoom().getNumber());
                        System.out.printf("%30s%30s%30s%30s\n", reservation.getReservationId(), reservation.getStartDate().toString(), reservation.getEndDate().toString(), reservationRoom.getRoom().getNumber().toString());
                    }
                }
            }
            System.out.println();
            while (true) {
                System.out.println("*** HoRS System :: Check-In Guest ***\n");
                System.out.println("1: Resolve Exception Record");
                System.out.println("2: Back");
                int response = 0;

                while (response < 1 || response > 2) {
                    System.out.print("> ");

                    response = scanner.nextInt();

                    if (response == 1) {
                        doResolveExceptionRecord();
                    } else if (response == 2) {
                        break;
                    } else {
                        System.out.println("Invalid Option!");
                    }
                }
                if (response == 2) {
                    break;
                }
            }

        } catch (GuestNotFoundException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    public void doResolveExceptionRecord() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** HoRS System :: Resolve Exception ***\n");
        System.out.print("Enter Exception ID > ");
        Long id = sc.nextLong();
        System.out.print("Resolve Exception? (Enter 'Y' to confirm) > ");
        String input = sc.next().trim();
        if (input.equals("Y")) {
            try {
                ExceptionRecordEntity exception = exceptionRecordSessionBean.retrieveExceptionRecordByExceptionRecordId(id);
                exception.setResolved(true);
                exceptionRecordSessionBean.updateExceptionRecord(exception);
                System.out.println("Exception " + exception.getExceptionRecordId() + " has been resolved!");
                System.out.print("Press any key to continue...> ");
                sc.nextLine();
            } catch (ExceptionRecordNotFoundException ex) {
                System.out.println("Error: " + ex.getMessage());
            } catch (InputDataValidationException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    public void doCheckOutGuest() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("*** HoRS System :: Check-Out Guest ***\n");
            System.out.print("Guest Email > ");
            String guestEmail = scanner.nextLine().trim();
            GuestEntity guest = guestSessionBean.retreiveGuestReservations(guestEmail);
            for (ReservationEntity reservation : guest.getReservations()) {
                for (ReservationRoomEntity reservationRoom : reservation.getReservationRooms()) {
                    RoomEntity room = reservationRoom.getRoom();
                    if (room != null) {
                        room.setRoomAllocated(false);
                        try {
                            roomSessionBean.updateRoom(room);
                        } catch (RoomNotFoundException | UpdateRoomException | InputDataValidationException ex) {
                            System.out.println("Error: " + ex.getMessage());
                        }
                    }
                }
            }
            System.out.println("");
            System.out.println("Guest " + guest.getName() + " successfully checked out!");
            System.out.print("Press any key to continue...> ");
            scanner.nextLine();
        } catch (GuestNotFoundException ex) {
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
    
    public LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

}
