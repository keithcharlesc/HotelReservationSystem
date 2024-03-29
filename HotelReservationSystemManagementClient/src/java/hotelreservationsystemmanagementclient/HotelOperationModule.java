package hotelreservationsystemmanagementclient;

import ejb.session.stateless.ExceptionRecordSessionBeanRemote;
import ejb.session.stateless.ReservationRoomSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.EmployeeEntity;
import entity.ExceptionRecordEntity;
import entity.NormalRateEntity;
import entity.PeakRateEntity;
import entity.PromotionRateEntity;
import entity.PublishedRateEntity;
import entity.ReservationRoomEntity;
import entity.RoomEntity;
import entity.RoomRateEntity;
import entity.RoomTypeEntity;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.EmployeeAccessRightEnum;
import util.enumeration.RoomStatusEnum;
import util.exception.DeleteRoomException;
import util.exception.DeleteRoomTypeException;
import util.exception.InputDataValidationException;
import util.exception.InvalidAccessRightException;
import util.exception.ReservationRoomNotFoundException;
import util.exception.RoomNotFoundException;
import util.exception.RoomNumberExistException;
import util.exception.RoomRateNameExistException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeNameExistException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomException;
import util.exception.UpdateRoomRateException;
import util.exception.UpdateRoomTypeException;

//need another system timer also 
public class HotelOperationModule {

    private RoomTypeSessionBeanRemote roomTypeSessionBean;
    private RoomSessionBeanRemote roomSessionBean;
    private RoomRateSessionBeanRemote roomRateSessionBean;
    private ExceptionRecordSessionBeanRemote exceptionRecordSessionBean;
    private EmployeeEntity currentEmployee;
    private ReservationRoomSessionBeanRemote reservationRoomSessionBean;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public HotelOperationModule() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public HotelOperationModule(RoomTypeSessionBeanRemote roomTypeSessionBean, RoomSessionBeanRemote roomSessionBean, RoomRateSessionBeanRemote roomRateSessionBean, ExceptionRecordSessionBeanRemote exceptionRecordSessionBean, ReservationRoomSessionBeanRemote reservationRoomSessionBean, EmployeeEntity curremtEmployee) {
        this();
        this.reservationRoomSessionBean = reservationRoomSessionBean;
        this.roomTypeSessionBean = roomTypeSessionBean;
        this.roomSessionBean = roomSessionBean;
        this.roomRateSessionBean = roomRateSessionBean;
        this.exceptionRecordSessionBean = exceptionRecordSessionBean;
        this.currentEmployee = curremtEmployee;
    }

    public void menuHotelOperation() throws InvalidAccessRightException {

        if (currentEmployee.getEmployeeAccessRightEnum() != EmployeeAccessRightEnum.OPERATION_MANAGER
                && currentEmployee.getEmployeeAccessRightEnum() != EmployeeAccessRightEnum.SALES_MANAGER && currentEmployee.getEmployeeAccessRightEnum() != EmployeeAccessRightEnum.SYSTEM_ADMINISTRATOR) {
            throw new InvalidAccessRightException("You don't have OPERATION MANAGER or SALES MANAGER or SYSTEM ADMINISTRATOR rights to access the HOTEL OPERATIONS module.");
        }
        
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** HoRS System :: Hotel Operation ***\n");
            System.out.println("1: Operation Manager");
            System.out.println("2: Sales Manager");
            System.out.println("3: System");
            System.out.println("4: Back\n");
            response = 0;

            while (response < 1 || response > 4) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    operationManagerOperations();
                } else if (response == 2) {
                    salesManagerOperations();
                } else if (response == 3) {
                  system();
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

    //OPERATION MANAGER OPERATIONS (START) =========================================>
    public void operationManagerOperations() throws InvalidAccessRightException {
        
        if (currentEmployee.getEmployeeAccessRightEnum() != EmployeeAccessRightEnum.OPERATION_MANAGER) {
            throw new InvalidAccessRightException("You don't have OPERATION MANAGER access rights.");
        }
        
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** HoRS System :: Hotel Operation :: Operation Manager ***\n");
            System.out.println("1: Create New Room Type");
            System.out.println("2: View Room Type Details"); //include update and delete
            System.out.println("3: View All Room Types");
            System.out.println("4: Create New Room");
            System.out.println("5: Update Room");
            System.out.println("6: Delete Room");
            System.out.println("7: View All Rooms");
            System.out.println("8: View Room Allocation Exception Report");
            System.out.println("9: Back\n");
            response = 0;

            while (response < 1 || response > 9) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    doCreateNewRoomType();
                } else if (response == 2) {
                    doViewRoomTypeDetails();
                } else if (response == 3) {
                    doViewAllRoomTypes();
                } else if (response == 4) {
                    doCreateNewRoom();
                } else if (response == 5) {
                    doUpdateRoom();
                } else if (response == 6) {
                    doDeleteRoom();
                } else if (response == 7) {
                    doViewAllRooms();
                } else if (response == 8) {
                    doViewRoomAllocationExceptionReport();
//                   roomAllocation();
                } else if (response == 9) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 9) {
                break;
            }
        }
    }

    /*----------------------------------------Room Type----------------------------------------*/
    public void doCreateNewRoomType() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** HoRS System :: Hotel Operation :: Operation Manager :: Create New Room Type ***\n");
        System.out.print("Enter Name of New Room Type> ");
        String roomTypeName = scanner.nextLine().trim();
        System.out.print("Enter the Next Room Type level following " + roomTypeName + "> ");
        String nextRoomType = scanner.nextLine().trim();
        RoomTypeEntity roomType = new RoomTypeEntity();
        roomType.setRoomTypeName(roomTypeName);
        roomType.setNextRoomType(nextRoomType);

        Set<ConstraintViolation<RoomTypeEntity>> constraintViolations = validator.validate(roomType);
        if (constraintViolations.isEmpty()) {
            try {
                RoomTypeEntity roomTypeInserted = roomTypeSessionBean.insertNewRoomType(roomType);
                System.out.println("Room Type Successfully Created : " + roomTypeInserted.getRoomTypeName() + " , ID : " + roomTypeInserted.getRoomTypeId());
            } catch (InputDataValidationException | RoomTypeNameExistException | UnknownPersistenceException | UpdateRoomTypeException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        } else {
            showInputDataValidationErrorsForRoomTypeEntity(constraintViolations);
        }
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }

    public void doViewRoomTypeDetails() {
        try {
            Scanner scanner = new Scanner(System.in);
            int response = 0;
            System.out.println("*** HoRS System :: Hotel Operation :: Operation Manager :: View Room Type Details ***\n");
            System.out.print("Enter Name of Room Type> ");
            String roomTypeName = scanner.nextLine().trim();
            RoomTypeEntity roomType = roomTypeSessionBean.retrieveRoomTypeByRoomTypeName(roomTypeName);
            System.out.printf("%19s%35s%20s%30s\n", "Room Type ID", "Room Type Name", "Is Disabled", "Next Room Type");
            System.out.printf("%19s%35s%20s%30s\n", roomType.getRoomTypeId().toString(), roomType.getRoomTypeName(), roomType.getIsDisabled(), roomType.getNextRoomType());
            System.out.println("1: Update Room Type");
            System.out.println("2: Delete Room Type");
            System.out.println("3: Back\n");
            System.out.print("> ");
            response = scanner.nextInt();
            if (response == 1) {
                doUpdateRoomType(roomType);
            } else if (response == 2) {
                doDeleteRoomType(roomType);
            }
        } catch (RoomTypeNotFoundException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    public void doUpdateRoomType(RoomTypeEntity roomType) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Room Type Name (blank if no change)> ");
        String name = "";
        name = scanner.nextLine().trim();
        if (name.length() > 0) {
            roomType.setRoomTypeName(name);
        }
        System.out.print("Enter Room Description of Room Type (blank if no change)> ");
        String description = "";
        description = scanner.nextLine().trim();
        if (description.length() > 0) {
            roomType.setRoomDescription(description);
        }
        System.out.print("Enter Room Size of Room Type (-1 if no change)> ");
        int size = -1;
        size = scanner.nextInt();
        if (size > 0) {
            roomType.setRoomSize(size);
        }
        System.out.print("Enter No. of Room Bed of Room Type (-1 if no change)> ");
        int bed = -1;
        bed = scanner.nextInt();
        if (bed > 0) {
            roomType.setRoomBed(bed);
        }
        System.out.print("Enter Room Capacity of RoomType (-1 if no change)> ");
        int capacity = -1;
        capacity = scanner.nextInt();
        scanner.nextLine();
        if (capacity > 0) {
            roomType.setRoomCapacity(capacity);
        }
        System.out.print("Enter Room Amenities of RoomType (blank if no change)> ");
        String amenities = "";
        amenities = scanner.nextLine().trim();
        if (amenities.length() > 0) {
            roomType.setRoomAmenities(amenities);
        }
        System.out.print("Enter Next Room Type of RoomType (blank if no change)> ");
        String nextRoomType = "";
        nextRoomType = scanner.nextLine().trim();
        if (nextRoomType.length() > 0) {
            roomType.setNextRoomType(nextRoomType);
        }
        try {
            roomTypeSessionBean.updateRoomType(roomType);
            System.out.println("Room Type updated!");
            System.out.print("Press any key to continue...> ");
            scanner.nextLine();
        } catch (RoomTypeNotFoundException | UpdateRoomTypeException | InputDataValidationException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    public void doDeleteRoomType(RoomTypeEntity roomType) {
        Scanner scanner = new Scanner(System.in);
        System.out.printf("Confirm Delete Room Type %s (Room Type ID: %d) (Enter 'Y' to Delete)> ", roomType.getRoomTypeName(), roomType.getRoomTypeId());
        String input = scanner.nextLine().trim();
        if (input.equals("Y")) {
            try {
                roomTypeSessionBean.deleteRoomType(roomType.getRoomTypeId());
                System.out.println("Room Type successfully deleted!");
                System.out.print("Press any key to continue...> ");
                scanner.nextLine();
            } catch (RoomTypeNotFoundException ex) {
                System.out.println("Error: " + ex.getMessage());
            } catch (DeleteRoomTypeException ex) {
                System.out.println("Error: " + ex.getMessage());
            } catch (UpdateRoomTypeException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        } else {
            System.out.println("Room Type NOT deleted!\n");
        }
    }

    public void doViewAllRoomTypes() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** HoRS System :: Hotel Operation :: Operation Manager :: View All Room Types ***\n");
        List<RoomTypeEntity> roomTypes = roomTypeSessionBean.retrieveAllRoomTypes();
        System.out.printf("%19s%30s%20s%30s\n", "Room Type ID", "Room Type Name", "Is Disabled", "Next Room Type");
        for (RoomTypeEntity roomType : roomTypes) {
            System.out.printf("%19s%30s%20s%30s\n", roomType.getRoomTypeId().toString(), roomType.getRoomTypeName(), roomType.getIsDisabled(), roomType.getNextRoomType());
        }
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }

    private void showInputDataValidationErrorsForRoomTypeEntity(Set<ConstraintViolation<RoomTypeEntity>> constraintViolations) {
        System.out.println("\nInput data validation error!:");

        for (ConstraintViolation constraintViolation : constraintViolations) {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }

    /*----------------------------------------Room----------------------------------------*/
    public void doCreateNewRoom() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** HoRS System :: Hotel Operation :: Operation Manager :: Create New Room ***\n");
        System.out.print("Enter Room Number of New Room > ");
        String roomNumber = scanner.nextLine().trim();
        System.out.print("Enter the Room Type > ");
        String roomType = scanner.nextLine().trim();
        RoomEntity room = new RoomEntity();
        room.setNumber(roomNumber);
        room.setRoomStatusEnum(RoomStatusEnum.AVAILABLE);

        Set<ConstraintViolation<RoomEntity>> constraintViolations = validator.validate(room);
        if (constraintViolations.isEmpty()) {
            try {
                RoomEntity roomCreated = roomSessionBean.createNewRoom(room, roomType);
                System.out.println("Room Successfully Created : Room No. " + roomCreated.getNumber() + " , Room Type: " + roomType + " , ID : " + roomCreated.getRoomId());
            } catch (InputDataValidationException | RoomNumberExistException | UnknownPersistenceException | RoomTypeNotFoundException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        } else {
            showInputDataValidationErrorsForRoomEntity(constraintViolations);
        }
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }

    public void doUpdateRoom() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** HoRS System :: Hotel Operation :: Operation Manager :: Update Room ***\n");
        System.out.print("Enter Room Number > ");
        String roomNumber = scanner.nextLine().trim();
        try {
            RoomEntity room = roomSessionBean.retrieveRoomByRoomNumber(roomNumber);
            System.out.print("Enter new Room Number (blank if no change) >");
            String newRoomNumber = scanner.nextLine().trim();
            if (newRoomNumber.length() > 0) {
                room.setNumber(roomNumber);
            }
            while (true) {
                System.out.print("Select Room Status (0: No Change, 1: Available, 2: Unavailable) > ");
                Integer roomStatusInt = scanner.nextInt();
                if (roomStatusInt >= 1 && roomStatusInt <= 2) {
                    room.setRoomStatusEnum(RoomStatusEnum.values()[roomStatusInt - 1]);
                    break;
                } else if (roomStatusInt == 0) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }
            Set<ConstraintViolation<RoomEntity>> constraintViolations = validator.validate(room);
            if (constraintViolations.isEmpty()) {
                try {
                    RoomEntity roomUpdated = roomSessionBean.updateRoom(room);
                    System.out.println("Room Successfully Updated! : Room No.: " + roomUpdated.getNumber() + ", Room Type: " + roomUpdated.getRoomType().getRoomTypeName() + ", ID : " + roomUpdated.getRoomId());
                } catch (InputDataValidationException | UpdateRoomException ex) {
                    System.out.println("Error: " + ex.getMessage());
                }
            } else {
                showInputDataValidationErrorsForRoomEntity(constraintViolations);
            }
        } catch (RoomNotFoundException ex) {
            System.out.println("Error: " + ex.getMessage());
        }

        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }

    public void doDeleteRoom() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** HoRS System :: Hotel Operation :: Operation Manager :: Delete Room ***\n");
        System.out.print("Enter Room Number of Room to be deleted > ");
        String roomNumber = scanner.nextLine().trim();
        try {
            RoomEntity room = roomSessionBean.retrieveRoomByRoomNumber(roomNumber);
            System.out.printf("Confirm Delete Room %s (Room ID: %d) (Enter 'Y' to Delete)> ", room.getNumber(), room.getRoomId());
            String input = scanner.nextLine().trim();
            if (input.equals("Y")) {
                try {
                    roomSessionBean.deleteRoom(room.getRoomId());
                    System.out.println("Room successfully deleted!");
                } catch (RoomNotFoundException ex) {
                    System.out.println("Error: " + ex.getMessage());
                } catch (DeleteRoomException ex) {
                    System.out.println("Error: " + ex.getMessage());
                }
            } else {
                System.out.println("Room NOT deleted!\n");
            }

        } catch (RoomNotFoundException ex) {
            System.out.println("Error: " + ex.getMessage());
        }

        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }

    public void doViewAllRooms() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** HoRS System :: Hotel Operation :: Operation Manager :: View All Rooms ***\n");
        List<RoomEntity> rooms = roomSessionBean.retrieveAllRooms();
        System.out.printf("%20s%20s%30s\n", "Room Number", "Room Status", "Room Type");
        for (RoomEntity room : rooms) {
            System.out.printf("%20s%20s%30s\n", room.getNumber(), room.getRoomStatusEnum().toString(), room.getRoomType().getRoomTypeName());
        }
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }

    private void showInputDataValidationErrorsForRoomEntity(Set<ConstraintViolation<RoomEntity>> constraintViolations) {
        System.out.println("\nInput data validation error!:");

        for (ConstraintViolation constraintViolation : constraintViolations) {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }

    /*----------------------------------------Room Allocation Exception Report----------------------------------------*/
    private void doViewRoomAllocationExceptionReport() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** HoRS System :: Hotel Operation :: Operation Manager :: View Room Allocation Exception Report ***\n");
        List<ExceptionRecordEntity> exceptionRecords = exceptionRecordSessionBean.retrieveAllExceptionRecords();
        System.out.println("Exception Type [1] => No available room for reserved room type, upgrade to next higher room type is available (Room is automatically allocated by system)");
        System.out.println("Exception Type [2] => No available room for reserved room type, no upgrade to next higher room type is available (No room automatically allocated by system)");
        System.out.printf("%30s%30s%30s%30s\n", "Reservation ID", "Exception Record ID", "Exception Type", "Resolved Status");
        if (!exceptionRecords.isEmpty()) {
            for (ExceptionRecordEntity exceptionRecord : exceptionRecords) {
                ReservationRoomEntity reservationRoom = exceptionRecord.getReservationRoom();
                Long reservationId = reservationRoom.getReservation().getReservationId();
                System.out.printf("%30s%30s%30s%30s\n", reservationId, exceptionRecord.getExceptionRecordId(), exceptionRecord.getTypeOfException(), exceptionRecord.getResolved());
            }
        }
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
    // OPERATION MANAGER OPERATIONS (END) <================================================ 
    //SALES MANAGER OPERATIONS (START) ===================================================>  

    public void salesManagerOperations() throws InvalidAccessRightException {

        if (currentEmployee.getEmployeeAccessRightEnum() != EmployeeAccessRightEnum.SALES_MANAGER) {
            throw new InvalidAccessRightException("You don't have SALES MANAGER access rights.");
        }

        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** HoRS System :: Hotel Operation :: Sales Manager ***\n");
            System.out.println("1: Create New Room Rate");
            System.out.println("2: View Room Rate Details"); //include update and delete
            System.out.println("3: View All Room Rates");
            System.out.println("4: Back\n");
            response = 0;

            while (response < 1 || response > 3) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    doCreateNewRoomRate();
                } else if (response == 2) {
                    doViewRoomRateDetails();
                } else if (response == 3) {
                    doViewAllRoomRates();
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

    // OPERATION MANAGER OPERATIONS (END) <================================================ 
    //SALES MANAGER OPERATIONS (START) ===================================================>  

    /*----------------------------------------Room Rate----------------------------------------*/
    public void doCreateNewRoomRate() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** HoRS System :: Create New Room Rate ***\n");

        System.out.print("Enter Name of Room Type> ");
        String roomTypeName = scanner.nextLine().trim();
        System.out.print("Enter Name of new Room Rate> ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter Rate Per Night of new Room Rate> ");
        Double rate = scanner.nextDouble();
        BigDecimal ratePerNight = BigDecimal.valueOf(rate);

        System.out.print("Select Type of Room Rate (1: Normal 2: Published 3: Promotion 4: Peak) > ");
        Integer roomRateType = scanner.nextInt();
        scanner.nextLine();

        RoomRateEntity roomRate;
        String start;
        String end;
        Boolean failedConstraint = true;

        if (roomRateType >= 1 && roomRateType <= 4) {
            try {
                if (roomRateType == 1) {
                    NormalRateEntity normalRate = new NormalRateEntity();
                    normalRate.setName(name);
                    normalRate.setRatePerNight(ratePerNight);
                    roomRate = normalRate;
                    
                    Set<ConstraintViolation<RoomRateEntity>> constraintViolations = validator.validate(roomRate);
                    if (constraintViolations.isEmpty()) {
                        failedConstraint=false;
                        roomRateSessionBean.createNewRoomRate(roomRate, roomTypeName);
                    } else {
                        showInputDataValidationErrorsForRoomRateEntity(constraintViolations);
                    }
                } else if (roomRateType == 2) {
                    PublishedRateEntity publishedRate = new PublishedRateEntity();
                    publishedRate.setName(name);
                    publishedRate.setRatePerNight(ratePerNight);
                    roomRate = publishedRate;
                    Set<ConstraintViolation<RoomRateEntity>> constraintViolations = validator.validate(roomRate);
                    if (constraintViolations.isEmpty()) {
                        failedConstraint = false;
                        roomRateSessionBean.createNewRoomRate(roomRate, roomTypeName);
                    } else {
                        showInputDataValidationErrorsForRoomRateEntity(constraintViolations);
                    }
                } else if (roomRateType == 3) {
                    PromotionRateEntity promotionRate = new PromotionRateEntity();
                    System.out.print("Enter Start Date of Validitory Period [yyyy-MM-dd] > ");
                    start = scanner.nextLine();
                    LocalDate startDate = LocalDate.parse(start);
                    LocalDateTime startDateTime = startDate.atStartOfDay();
                    Date validityStartDate = convertToDateViaSqlTimestamp(startDateTime);

                    System.out.print("Enter End Date of Validitory Period [yyyy-MM-dd]> ");
                    end = scanner.nextLine();
                    LocalDate endDate = LocalDate.parse(end);
                    LocalDateTime endDateTime = endDate.atStartOfDay();
                    Date validityEndDate = convertToDateViaSqlTimestamp(endDateTime.plusHours(23).plusMinutes(59));

                    promotionRate.setName(name);
                    promotionRate.setRatePerNight(ratePerNight);
                    promotionRate.setStartDate(validityStartDate);
                    promotionRate.setEndDate(validityEndDate);
                    roomRate = promotionRate;
                    
                    Set<ConstraintViolation<RoomRateEntity>> constraintViolations = validator.validate(roomRate);
                    if (constraintViolations.isEmpty()) {
                        failedConstraint=false;
                        roomRateSessionBean.createNewRoomRate(roomRate, roomTypeName);
                    } else {
                        showInputDataValidationErrorsForRoomRateEntity(constraintViolations);
                    }
                } else if (roomRateType == 4) {
                    PeakRateEntity peakRate = new PeakRateEntity();
                    System.out.print("Enter Start Date of Validity Period [yyyy-MM-dd] > ");
                    start = scanner.nextLine().trim();
                    LocalDate startDate = LocalDate.parse(start);
                    LocalDateTime startDateTime = startDate.atStartOfDay();
                    Date validityStartDate = convertToDateViaSqlTimestamp(startDateTime);
                    System.out.print("Enter End Date of Validity Period [yyyy-MM-dd]> ");
                    end = scanner.nextLine();
                    LocalDate endDate = LocalDate.parse(end);
                    LocalDateTime endDateTime = endDate.atStartOfDay();
                    Date validityEndDate = convertToDateViaSqlTimestamp(endDateTime.plusHours(23).plusMinutes(59));
                    peakRate.setName(name);
                    peakRate.setRatePerNight(ratePerNight);
                    peakRate.setStartDate(validityStartDate);
                    peakRate.setEndDate(validityEndDate);
                    roomRate = peakRate;
                    
                    Set<ConstraintViolation<RoomRateEntity>> constraintViolations = validator.validate(roomRate);
                    if (constraintViolations.isEmpty()) {
                        failedConstraint = false;
                        roomRateSessionBean.createNewRoomRate(roomRate, roomTypeName);
                    } else {
                        showInputDataValidationErrorsForRoomRateEntity(constraintViolations);
                    }
                }
                if (!failedConstraint) {
                    System.out.println("Room Rate created successfully!\n");
                    System.out.print("Press any key to continue...> ");
                    scanner.nextLine();
                }
            } catch (RoomRateNameExistException | UnknownPersistenceException | InputDataValidationException | RoomTypeNotFoundException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    public void doViewRoomRateDetails() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("*** HoRS System :: View Room Rate Details ***\n");

            System.out.print("Enter Name of Room Rate> ");
            String roomRateName = scanner.nextLine().trim();
            RoomRateEntity roomRate = roomRateSessionBean.retrieveRoomRateByRoomRateName(roomRateName);
            if (roomRate instanceof PeakRateEntity) {
                PeakRateEntity peakRate = (PeakRateEntity) roomRate;
                System.out.printf("%20s%30s%20s%30s%30s\n", "Room Rate ID", "Room Rate Name", "Rate Per Night", "Start Date", "End Date");
                System.out.printf("%20s%30s%20s%30s%30s\n", peakRate.getRoomRateId().toString(), peakRate.getName(), NumberFormat.getCurrencyInstance().format(peakRate.getRatePerNight()), peakRate.getStartDate().toString(), peakRate.getEndDate().toString());
            } else if (roomRate instanceof PromotionRateEntity) {
                PromotionRateEntity promotionRate = (PromotionRateEntity) roomRate;
                System.out.printf("%20s%30s%20s%30s%30s\n", "Room Rate ID", "Room Rate Name", "Rate Per Night", "Start Date", "End Date");
                System.out.printf("%20s%30s%20s%30s%30s\n", promotionRate.getRoomRateId().toString(), promotionRate.getName(), NumberFormat.getCurrencyInstance().format(promotionRate.getRatePerNight()), promotionRate.getStartDate().toString(), promotionRate.getEndDate().toString());
            } else {
                System.out.printf("%20s%30s%20s\n", "Room Rate ID", "Room Rate Name", "Rate Per Night");
                System.out.printf("%20s%30s%20s\n", roomRate.getRoomRateId().toString(), roomRate.getName(), NumberFormat.getCurrencyInstance().format(roomRate.getRatePerNight()));
            }

            RoomRateEntity roomRateToBeUpdated = roomRateSessionBean.retrieveRoomRateByRoomRateId(roomRate.getRoomRateId());

            System.out.println("1: Update Room Rate");
            System.out.println("2: Delete Room Rate");
            System.out.println("3: Back\n");
            System.out.print("> ");
            int response = scanner.nextInt();

            if (response == 1) {
                doUpdateRoomRate(roomRateToBeUpdated);
                System.out.print("Press any key to continue...> ");
                scanner.nextLine();
            } else if (response == 2) {
                doDeleteRoomRate(roomRateToBeUpdated);
                System.out.print("Press any key to continue...> ");
                scanner.nextLine();
            }

        } catch (RoomRateNotFoundException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    public void doViewAllRoomRates() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** HoRS System :: View All Room Rate Details ***\n");
        List<RoomRateEntity> roomRates = roomRateSessionBean.retrieveAllRoomRates();
        System.out.printf("%20s%35s%20s%30s%30s\n", "Room Rate ID", "Room Rate Name", "Rate Per Night", "Start Date", "End Date");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");  
        for (RoomRateEntity roomRate : roomRates) {
            if (roomRate instanceof PeakRateEntity) {
                PeakRateEntity peakRate = (PeakRateEntity) roomRate;
                System.out.printf("%20s%35s%20s%30s%30s\n", peakRate.getRoomRateId().toString(), peakRate.getName(), NumberFormat.getCurrencyInstance().format(peakRate.getRatePerNight()), dateFormat.format(peakRate.getStartDate()), dateFormat.format(peakRate.getEndDate()));
            } else if (roomRate instanceof PromotionRateEntity) {
                PromotionRateEntity promotionRate = (PromotionRateEntity) roomRate;
                System.out.printf("%20s%35s%20s%30s%30s\n", promotionRate.getRoomRateId().toString(), promotionRate.getName(), NumberFormat.getCurrencyInstance().format(promotionRate.getRatePerNight()), dateFormat.format(promotionRate.getStartDate()), dateFormat.format(promotionRate.getEndDate()));
            } else {
                System.out.printf("%20s%35s%20s\n", roomRate.getRoomRateId().toString(), roomRate.getName(), NumberFormat.getCurrencyInstance().format(roomRate.getRatePerNight()));
            }
        }

        System.out.print("Press any key to continue...> ");
        sc.nextLine();
    }

    private void doUpdateRoomRate(RoomRateEntity roomRate) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Rate Per Night (-1 if no change)> ");
        Double rate = scanner.nextDouble();
        scanner.nextLine();
        if (rate > 0) {
            BigDecimal changedRate = BigDecimal.valueOf(rate);
            roomRate.setRatePerNight(changedRate);
        }
        if (roomRate instanceof PromotionRateEntity) {
            System.out.print("Enter Start Date of Validity Period [yyyy-MM-dd] (blank if no change)> ");
            String start = scanner.nextLine().trim();
            if (start.length() > 0) {
                LocalDate startDate = LocalDate.parse(start);
                LocalDateTime startDateTime = startDate.atStartOfDay();
                Date validityStartDate = convertToDateViaSqlTimestamp(startDateTime);
                ((PromotionRateEntity) roomRate).setStartDate(validityStartDate);
            }
            System.out.print("Enter End Date of Validity Period [yyyy-MM-dd] (blank if no change)> ");
            String end = scanner.nextLine().trim();
            if (end.length() > 0) {
                LocalDate endDate = LocalDate.parse(end);
                LocalDateTime endDateTime = endDate.atStartOfDay();
                Date validityEndDate = convertToDateViaSqlTimestamp(endDateTime);
                ((PromotionRateEntity) roomRate).setEndDate(validityEndDate);
            }
        } else if (roomRate instanceof PeakRateEntity) {
            System.out.print("Enter Start Date of Validity Period [yyyy-MM-dd] (blank if no change)> ");
            String start = scanner.nextLine();
            if (start.length() > 0) {
                LocalDate startDate = LocalDate.parse(start);
                LocalDateTime startDateTime = startDate.atStartOfDay();
                Date validityStartDate = convertToDateViaSqlTimestamp(startDateTime);
                ((PeakRateEntity) roomRate).setStartDate(validityStartDate);
            }
            System.out.print("Enter End Date of Validity Period [yyyy-MM-dd] (blank if no change)> ");
            String end = scanner.nextLine();
            if (end.length() > 0) {
                LocalDate endDate = LocalDate.parse(end);
                LocalDateTime endDateTime = endDate.atStartOfDay();
                Date validityEndDate = convertToDateViaSqlTimestamp(endDateTime);
                ((PeakRateEntity) roomRate).setEndDate(validityEndDate);
            }
        }

        Set<ConstraintViolation<RoomRateEntity>> constraintViolations = validator.validate(roomRate);

        if (constraintViolations.isEmpty()) {
            try {
                if (roomRate instanceof PromotionRateEntity) {
                    roomRateSessionBean.updatePromotionRate((PromotionRateEntity) roomRate);
                } else if (roomRate instanceof PeakRateEntity) {
                    roomRateSessionBean.updatePeakRate((PeakRateEntity) roomRate);
                } else {
                    roomRateSessionBean.updateRoomRate(roomRate);
                }
                System.out.println("Room Rate updated successfully!\n");
                System.out.print("Press any key to continue...> ");
                scanner.nextLine();
            } catch (RoomRateNotFoundException ex) {
                System.out.println("Error when updating Room Rate: " + ex.getMessage());
            } catch (UpdateRoomRateException ex) {
                System.out.println("Error when updating Room Rate: " + ex.getMessage());
            } catch (InputDataValidationException ex) {
                System.out.println("Error when updating Room Rate: " + ex.getMessage());
            }
        } else {
            showInputDataValidationErrorsForRoomRateEntity(constraintViolations);
        }
    }

    private void doDeleteRoomRate(RoomRateEntity roomRate) {
        Scanner scanner = new Scanner(System.in);
        String input;

        System.out.println("*** HoRS System :: View Room Rate Details :: Delete Room Rate ***\n");
        System.out.printf("Confirm Delete Room Rate %s (ID: %s) (Enter 'Y' to Delete)> ", roomRate.getName(), roomRate.getRoomRateId());
        input = scanner.nextLine().trim();

        if (input.equals("Y")) {
            try {
                roomRate.setIsDisabled(true);
                if (roomRate instanceof PromotionRateEntity) {
                    roomRateSessionBean.updatePromotionRate((PromotionRateEntity) roomRate);
                } else if (roomRate instanceof PeakRateEntity) {
                    roomRateSessionBean.updatePeakRate((PeakRateEntity) roomRate);
                } else {
                    roomRateSessionBean.updateRoomRate(roomRate);
                }
                System.out.println("Room rate deleted successfully!\n");
                System.out.print("Press any key to continue...> ");
                scanner.nextLine();
            } catch (UpdateRoomRateException | RoomRateNotFoundException | InputDataValidationException ex) {
                System.out.println("An error has occurred while deleting product: " + ex.getMessage() + "\n");;
            }
        } else {
            System.out.println("No reservation made!\n");
        }
    }

    private void showInputDataValidationErrorsForRoomRateEntity(Set<ConstraintViolation<RoomRateEntity>> constraintViolations) {
        System.out.println("\nInput data validation error!:");

        for (ConstraintViolation constraintViolation : constraintViolations) {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

    }

    public Date convertToDateViaSqlTimestamp(LocalDateTime dateToConvert) {
        return java.sql.Timestamp.valueOf(dateToConvert);
    }
    // SALES MANAGER OPERATIONS (END) <==============================================

    //SYSTEM (START) ===============================================================> 
    public void roomAllocation() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** HoRS System :: Room Allocation ***\n");
        System.out.print("Enter Date of Allocation [yyyy-MM-dd]> "); 
        String allocation = scanner.nextLine().trim();
        LocalDate allocateDate = LocalDate.parse(allocation);
        LocalDateTime allocationDateTime = allocateDate.atStartOfDay();
        Date allocationStartDate = convertToDateViaSqlTimestamp(allocationDateTime);
        try {
            reservationRoomSessionBean.allocateRooms(allocationStartDate);
            reservationRoomSessionBean.allocateRoomExceptionType1(allocationStartDate);
            reservationRoomSessionBean.allocateRoomExceptionType2(allocationStartDate);
            System.out.println("Room Allocation successfully!\n");
            System.out.print("Press any key to continue...> ");
            scanner.nextLine();
        } catch (ReservationRoomNotFoundException ex) {
            System.out.println("Error: " + ex.getMessage());
        } catch (RoomTypeNotFoundException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
    
    public void system() throws InvalidAccessRightException {
        
        if (currentEmployee.getEmployeeAccessRightEnum() != EmployeeAccessRightEnum.SYSTEM_ADMINISTRATOR) {
            throw new InvalidAccessRightException("You don't have SYSTEM ADMINISTRATOR access rights.");
        }
        
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** HoRS System :: Hotel Operation :: System ***\n");
            System.out.println("1: Room Allocation");
            System.out.println("2: Back"); 
            response = 0;

            while (response < 1 || response > 2) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    roomAllocation();
                } else if (response == 2) {
                    break;
                }
            }
            if(response==2) {
                break;
            }
        }
    }
    //SYSTEM (END) <=================================================================
}
