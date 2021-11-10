package hotelreservationsystemmanagementclient;

import ejb.session.stateless.ExceptionRecordSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.EmployeeEntity;
import entity.NormalRateEntity;
import entity.PeakRateEntity;
import entity.PromotionRateEntity;
import entity.PublishedRateEntity;
import entity.RoomRateEntity;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.EmployeeAccessRightEnum;
import util.exception.InputDataValidationException;
import util.exception.InvalidAccessRightException;
import util.exception.RoomRateNameExistException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomRateException;

//need another system timer also 
public class HotelOperationModule {

    private RoomTypeSessionBeanRemote roomTypeSessionBean;
    private RoomSessionBeanRemote roomSessionBean;
    private RoomRateSessionBeanRemote roomRateSessionBean;
    private ExceptionRecordSessionBeanRemote exceptionRecordSessionBean;
    private EmployeeEntity currentEmployee;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public HotelOperationModule() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public HotelOperationModule(RoomTypeSessionBeanRemote roomTypeSessionBean, RoomSessionBeanRemote roomSessionBean, RoomRateSessionBeanRemote roomRateSessionBean, ExceptionRecordSessionBeanRemote exceptionRecordSessionBean, EmployeeEntity curremtEmployee) {
        this();
        this.roomTypeSessionBean = roomTypeSessionBean;
        this.roomSessionBean = roomSessionBean;
        this.roomRateSessionBean = roomRateSessionBean;
        this.exceptionRecordSessionBean = exceptionRecordSessionBean;
        this.currentEmployee = curremtEmployee;
    }

    public void menuHotelOperation() throws InvalidAccessRightException {

        if (currentEmployee.getEmployeeAccessRightEnum() != EmployeeAccessRightEnum.OPERATION_MANAGER && currentEmployee.getEmployeeAccessRightEnum() != EmployeeAccessRightEnum.SALES_MANAGER) {
            throw new InvalidAccessRightException("You don't have OPERATION MANAGER or SALES MANAGER rights to access the system administration module.");
        }

        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** HoRS System :: System Administration ***\n");
            System.out.println("1: Operation Manager");
            System.out.println("2: Sales Manager");
            System.out.println("3: System");
            System.out.println("4: Back\n");
            response = 0;

            while (response < 1 || response > 4) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
//                    doCreateNewEmployee();
                } else if (response == 2) {
                    salesManagerOperations();
                } else if (response == 3) {
//                    doCreateNewPartner();
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

    public void salesManagerOperations() throws InvalidAccessRightException {

        if (currentEmployee.getEmployeeAccessRightEnum() != EmployeeAccessRightEnum.SALES_MANAGER) {
            throw new InvalidAccessRightException("You don't have OPERATION MANAGER or SALES MANAGER rights to access the system administration module.");
        }

        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** HoRS System :: Sales Manager Operation ***\n");
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

//    public void operationManagerOperations() {
//        Scanner scanner = new Scanner(System.in);
//        Integer response = 0;
//        
//        while(true)
//        {
//            System.out.println("*** HoRS System :: Operation Manager Operation ***\n");
//            System.out.println("1: Create New Room Type"); 
//            System.out.println("2: View Room Type Details"); //include update and delete
//            System.out.println("3: View All Room Types");
//            System.out.println("4: Create New Room");
//            System.out.println("5: Update Room");
//            System.out.println("6: Delete Room");
//            System.out.println("7: View All Rooms");
//            System.out.println("8: View Room Allocation Exception Report");
//            System.out.println("9: Back\n");
//            response = 0;
//            
//            while(response < 1 || response > 9)
//            {
//                System.out.print("> ");
//
//                response = scanner.nextInt();
//
//                if(response == 1)
//                {
//                    doCreateNewRoomType();
//                }
//                else if(response == 2)
//                {
//                    doViewRoomTypeDetails();
//                }
//                else if(response == 3)
//                {
//                    doViewAllRoomTypes();
//                }
//                else if(response == 4)
//                {
//                    doCreateNewRoom();
//                }
//                else if(response == 5)
//                {
//                    doUpdateRoom();
//                }
//                else if(response == 6)
//                {
//                    doDeleteRoom();
//                }
//                else if(response == 7)
//                {
//                    doViewAllRooms();
//                }
//                else if(response == 8)
//                {
//                    doViewRoomAllocationExceptionReport();
//                }
//                else if(response == 9)
//                {
//                    break;
//                }
//                else
//                {
//                    System.out.println("Invalid option, please try again!\n");                
//                }
//            }
//            
//            if(response == 9)
//            {
//                break;
//            }
//        }
//    }
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

        if (roomRateType >= 1 && roomRateType <= 4) {
            try {
                if (roomRateType == 1) {
                    NormalRateEntity normalRate = new NormalRateEntity();
                    normalRate.setName(name);
                    normalRate.setRatePerNight(ratePerNight);
                    roomRate = normalRate;
                    Set<ConstraintViolation<RoomRateEntity>> constraintViolations = validator.validate(roomRate);

                    if (constraintViolations.isEmpty()) {
                        roomRateSessionBean.createNewRoomRate(roomRate, roomTypeName);
                    }
                } else if (roomRateType == 2) {
                    PublishedRateEntity publishedRate = new PublishedRateEntity();
                    publishedRate.setName(name);
                    publishedRate.setRatePerNight(ratePerNight);
                    roomRate = publishedRate;
                    Set<ConstraintViolation<RoomRateEntity>> constraintViolations = validator.validate(roomRate);

                    if (constraintViolations.isEmpty()) {
                        roomRateSessionBean.createNewRoomRate(roomRate, roomTypeName);
                    }
                } else if (roomRateType == 3) {
                    PromotionRateEntity promotionRate = new PromotionRateEntity();
                    System.out.print("Enter Start Date of Validitory Period [yyyy-MM-dd] > ");
                    start = scanner.nextLine();
                    LocalDate startDate = LocalDate.parse(start);
                    LocalDateTime startDateTime = startDate.atStartOfDay();
                    Date validityStartDate = convertToDateViaSqlTimestamp(startDateTime);
//                        Date startDate = new SimpleDateFormat("dd/MM/yyyy").parse(start);
////                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
////                        LocalDateTime startDateTime = LocalDateTime.parse(startDate, formatter);
                    System.out.print("Enter End Date of Validitory Period [yyyy-MM-dd]> ");
                    end = scanner.nextLine();
                    LocalDate endDate = LocalDate.parse(end);
                    LocalDateTime endDateTime = endDate.atStartOfDay();
                    Date validityEndDate = convertToDateViaSqlTimestamp(endDateTime);
//                        Date endDate = new SimpleDateFormat("dd/MM/yyyy").parse(end);
//                        //LocalDateTime endDateTime = LocalDateTime.parse(endDate, formatter);                        promotionRate.setName(name);
                    promotionRate.setName(name);
                    promotionRate.setRatePerNight(ratePerNight);
                    promotionRate.setStartDate(validityStartDate);
                    promotionRate.setEndDate(validityEndDate);
                    roomRate = promotionRate;
                    Set<ConstraintViolation<RoomRateEntity>> constraintViolations = validator.validate(roomRate);

                    if (constraintViolations.isEmpty()) {
                        roomRateSessionBean.createNewRoomRate(roomRate, roomTypeName);
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
                    Date validityEndDate = convertToDateViaSqlTimestamp(endDateTime);
                    peakRate.setName(name);
                    peakRate.setRatePerNight(ratePerNight);
                    peakRate.setStartDate(validityStartDate);
                    peakRate.setEndDate(validityEndDate);
                    roomRate = peakRate;
                    Set<ConstraintViolation<RoomRateEntity>> constraintViolations = validator.validate(roomRate);

                    if (constraintViolations.isEmpty()) {
                        roomRateSessionBean.createNewRoomRate(roomRate, roomTypeName);
                    }
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
        System.out.printf("%20s%30s%20s%20s%20s\n", "Room Rate ID", "Room Rate Name", "Rate Per Night", "Start Date", "End Date");
        for (RoomRateEntity roomRate : roomRates) {
            if (roomRate instanceof PeakRateEntity) {
                PeakRateEntity peakRate = (PeakRateEntity) roomRate;
                //System.out.printf("%20s%30s%20s%20s%20s\n", "Room Rate ID", "Room Rate Name", "Rate Per Night", "Start Date", "End Date");
                System.out.printf("%20s%30s%20s%30s%30s\n", peakRate.getRoomRateId().toString(), peakRate.getName(), NumberFormat.getCurrencyInstance().format(peakRate.getRatePerNight()), peakRate.getStartDate().toString(), peakRate.getEndDate().toString());
            } else if (roomRate instanceof PromotionRateEntity) {
                PromotionRateEntity promotionRate = (PromotionRateEntity) roomRate;
                //System.out.printf("%20s%30s%20s%20s%20s\n", "Room Rate ID", "Room Rate Name", "Rate Per Night", "Start Date", "End Date");
                System.out.printf("%20s%30s%20s%30s%30s\n", promotionRate.getRoomRateId().toString(), promotionRate.getName(), NumberFormat.getCurrencyInstance().format(promotionRate.getRatePerNight()), promotionRate.getStartDate().toString(), promotionRate.getEndDate().toString());
            } else {
                //System.out.printf("%12s%30s%20s\n", "Room Rate ID", "Room Rate Name", "Rate Per Night");
                System.out.printf("%20s%30s%20s\n", roomRate.getRoomRateId().toString(), roomRate.getName(), NumberFormat.getCurrencyInstance().format(roomRate.getRatePerNight()));
            }
        }
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
                System.out.println("RoomRate updated successfully!\n");
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

    private void showInputDataValidationErrorsForRoomRateEntity(Set<ConstraintViolation<RoomRateEntity>> constraintViolations) {
        System.out.println("\nInput data validation error!:");

        for (ConstraintViolation constraintViolation : constraintViolations) {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }

//wrong check session bean 
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
            } catch (UpdateRoomRateException | RoomRateNotFoundException | InputDataValidationException ex) {
                System.out.println("An error has occurred while deleting product: " + ex.getMessage() + "\n");;
            }
        } else {
            System.out.println("Product NOT deleted!\n");
        }
    }

    public Date convertToDateViaSqlTimestamp(LocalDateTime dateToConvert) {
        return java.sql.Timestamp.valueOf(dateToConvert);
    }
}
