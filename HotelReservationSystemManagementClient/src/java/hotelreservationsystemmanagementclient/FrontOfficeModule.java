package hotelreservationsystemmanagementclient;

import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.EmployeeEntity;
import entity.PublishedRateEntity;
import entity.RoomRateEntity;
import entity.RoomTypeEntity;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.EmployeeAccessRightEnum;
import util.exception.InvalidAccessRightException;
import util.exception.RoomTypeNotFoundException;

public class FrontOfficeModule {

    private RoomSessionBeanRemote roomSessionBean;
    private RoomTypeSessionBeanRemote roomTypeSessionBean;
    private GuestSessionBeanRemote guestSessionBean;
    private ReservationSessionBeanRemote reservationSessionBean;
    private EmployeeEntity currentEmployee;
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public FrontOfficeModule() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public FrontOfficeModule(RoomSessionBeanRemote roomSessionBean, RoomTypeSessionBeanRemote roomTypeSessionBean, GuestSessionBeanRemote guestSessionBean, ReservationSessionBeanRemote reservationSessionBean, EmployeeEntity currentEmployee) {
        this();
        this.roomSessionBean = roomSessionBean;
        this.roomTypeSessionBean = roomTypeSessionBean;
        this.guestSessionBean = guestSessionBean;
        this.reservationSessionBean = reservationSessionBean;
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
            System.out.println("4: Logout\n");
            response = 0;

            while (response < 1 || response > 3) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    doWalkInSearchRoom();
                } else if (response == 2) {
//                    doCheckInGuest();
                } else if (response == 3) {
//                    doCheckOutGuest();
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
//        System.out.println("Before adding 2pm, " + startDateTime);
        Date checkInDate = convertToDateViaSqlTimestamp(startDateTime.withHour(14)); //checkindate
//        System.out.println("After adding 2pm, " + checkInDate);
        System.out.print("Enter Check-Out Date [yyyy-MM-dd] > ");
        end = scanner.nextLine();
        LocalDate endDate = LocalDate.parse(end);
        LocalDateTime endDateTime = endDate.atStartOfDay();
//        System.out.println("Before adding 12pm, " + endDateTime);
        Date checkOutDate = convertToDateViaSqlTimestamp(endDateTime.withHour(12)); //checkoudate
//        System.out.println("After adding 12pm, " + checkOutDate);
        try {
            Long numberOfNights = (numberOfNights(checkInDate, checkOutDate)) + 1;
            //need to acccount for 12pm 2pm for check in and checkout to set it to
            System.out.println("Number of rooms > ");
            Integer numberOfRooms = scanner.nextInt();
            //call list of room types that have available rooms
//            System.out.println(roomTypeSessionBean.retrieveTotalQuantityOfRoomsBasedOnRoomType("Deluxe Room")); //total quantity of rooms per room type
            List<RoomTypeEntity> listOfRoomTypes = roomTypeSessionBean.retrieveAllRoomTypes();
            System.out.println("Available Room Types: ");
            for (RoomTypeEntity roomType : listOfRoomTypes) {
                int i = 1;
                Integer totalNoOfRoomsForRoomType = roomTypeSessionBean.retrieveTotalQuantityOfRoomsBasedOnRoomType(roomType.getRoomTypeName());
                Integer reservedRoomsForRoomTypeForDateRange = roomTypeSessionBean.retrieveQuantityOfRoomsReserved(checkInDate, checkOutDate, end);
                Integer remainingAvailableRooms = totalNoOfRoomsForRoomType - reservedRoomsForRoomTypeForDateRange;
                if (remainingAvailableRooms >= numberOfRooms) {
                    //if there is sufficient rooms available
                    //display the room type
                    System.out.println("(" + i + ") " + roomType.getRoomTypeName());
                }
            }
            System.out.println("Indicate Room Type Option> ");
            String roomTypeOption = scanner.nextLine().trim();
            try {
                RoomTypeEntity roomTypeEntity = roomTypeSessionBean.retrieveRoomTypeByRoomTypeName(roomTypeOption);
                List<RoomRateEntity> roomRates = roomTypeEntity.getRoomRates(); //jpql to get room rate = published rate
                RoomRateEntity publishedRate = new PublishedRateEntity();
                for (RoomRateEntity roomRate : roomRates) {
                    if (roomRate.getClass().getSimpleName().equals("PublishedRateEntity")) {
                        publishedRate = (PublishedRateEntity) roomRate;
                    }
                }
                BigDecimal reservationAmount = publishedRate.getRatePerNight().multiply(new BigDecimal(numberOfNights)); //convert number of nights to make it big decimal
                System.out.println("Reservation amount = " + reservationAmount);

            } catch (RoomTypeNotFoundException ex) {
                System.out.println("Error: " + ex.getMessage());
            }

//            System.out.println("numberOfNights (days difference): " + numberOfNights);
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
        }

    }

    public void doCheckInGuest() {

    }

    public void doCheckOutGuest() {

    }

    public Date convertToDateViaSqlTimestamp(LocalDateTime dateToConvert) {
        return java.sql.Timestamp.valueOf(dateToConvert);
    }

    public static long numberOfNights(Date firstDate, Date secondDate) throws IOException {
        return ChronoUnit.DAYS.between(firstDate.toInstant(), secondDate.toInstant());
    }

}
