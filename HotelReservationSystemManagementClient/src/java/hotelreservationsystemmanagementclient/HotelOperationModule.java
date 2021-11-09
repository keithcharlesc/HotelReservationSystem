/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.DeleteRoomRateException;
import util.exception.InputDataValidationException;
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
    private EmployeeEntity curremtEmployee;
    
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
        this.curremtEmployee = curremtEmployee;
    }
    
    public void salesManagerOperations() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** HoRS System :: Sales Manager Operation ***\n");
            System.out.println("1: Create New Room Rate");
            System.out.println("2: View Room Rate Details"); //include update and delete
            System.out.println("3: View All Room Rates");
            System.out.println("4: Back\n");
            response = 0;
            
            while(response < 1 || response > 3)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
//                    doCreateNewRoomRate();
                }
                else if(response == 2)
                {
//                    doViewRoomRateDetails();
                }
                else if(response == 3)
                {
//                    doViewAllRoomRates();
                }
                else if(response == 4)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 4)
            {
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
        
        while (true) {
            System.out.print("Select Type of Room Rate (1: Normal 2: Published 3: Promotion 4: Peak > ");
            Integer roomRateType = scanner.nextInt();

            RoomRateEntity roomRate;

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
                            break;
                        }
                    } else if (roomRateType == 2) {
                        PublishedRateEntity publishedRate = new PublishedRateEntity();
                        publishedRate.setName(name);
                        publishedRate.setRatePerNight(ratePerNight);
                        roomRate = publishedRate;
                        Set<ConstraintViolation<RoomRateEntity>> constraintViolations = validator.validate(roomRate);

                        if (constraintViolations.isEmpty()) {
                            roomRateSessionBean.createNewRoomRate(roomRate, roomTypeName);
                            break;
                        }
                    } else if (roomRateType == 3) {
                        PromotionRateEntity promotionRate = new PromotionRateEntity();
                        System.out.print("Enter Start Date of Validitory Period [yyyy-MM-dd HH:mm] > ");
                        String startDate = scanner.nextLine();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                        LocalDateTime startDateTime = LocalDateTime.parse(startDate, formatter);
                        System.out.print("Enter End Date of Validitory Period [yyyy-MM-dd HH:mm]> ");
                        String endDate = scanner.nextLine();
                        LocalDateTime endDateTime = LocalDateTime.parse(endDate, formatter);
                        promotionRate.setName(name);
                        promotionRate.setRatePerNight(ratePerNight);
                        promotionRate.setStartDate(startDateTime);
                        promotionRate.setEndDate(endDateTime);
                        roomRate = promotionRate;
                        Set<ConstraintViolation<RoomRateEntity>> constraintViolations = validator.validate(roomRate);

                        if (constraintViolations.isEmpty()) {
                            roomRateSessionBean.createNewRoomRate(roomRate, roomTypeName);
                            break;
                        }
                    } else if (roomRateType == 4) {
                        PeakRateEntity peakRate = new PeakRateEntity();
                        System.out.print("Enter Start Date of Validity Period [yyyy-MM-dd HH:mm] > ");
                        String startDate = scanner.nextLine();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                        LocalDateTime startDateTime = LocalDateTime.parse(startDate, formatter);
                        System.out.print("Enter End Date of Validity Period [yyyy-MM-dd HH:mm]> ");
                        String endDate = scanner.nextLine();
                        LocalDateTime endDateTime = LocalDateTime.parse(endDate, formatter);
                        peakRate.setName(name);
                        peakRate.setRatePerNight(ratePerNight);
                        peakRate.setStartDate(startDateTime);
                        peakRate.setEndDate(endDateTime);
                        roomRate = peakRate;
                        Set<ConstraintViolation<RoomRateEntity>> constraintViolations = validator.validate(roomRate);

                        if (constraintViolations.isEmpty()) {
                            roomRateSessionBean.createNewRoomRate(roomRate, roomTypeName);
                            break;
                        }
                    }
                } catch (RoomRateNameExistException | UnknownPersistenceException | InputDataValidationException | RoomTypeNotFoundException ex) {
                    System.out.println("Error: " + ex.getMessage());
                }
            }
        }
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }

    public void doViewRoomRateDetails() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("*** HoRS System :: View Room Rate Details ***\n");
            
            System.out.print("Enter Name of Room Rate> ");
            String roomRateName = scanner.nextLine().trim();
            RoomRateEntity roomRate = roomRateSessionBean.retrieveRoomRateByRoomRateName(roomRateName);
            if(roomRate instanceof PeakRateEntity) {
                PeakRateEntity peakRate = (PeakRateEntity) roomRate;
                System.out.printf("%19s%18s%17s%15s%31s\n", "Room Rate ID", "Room Rate Name", "Rate Per Night", "Start Date", "End Date");
                System.out.printf("%19s%18s%17s%15s%31s\n", peakRate.getRoomRateId(), peakRate.getName(), peakRate.getRatePerNight(), peakRate.getStartDate(), peakRate.getEndDate());
            } else if(roomRate instanceof PromotionRateEntity) {
                PromotionRateEntity promotionRate = (PromotionRateEntity) roomRate;
                System.out.printf("%19s%18s%17s%15s%31s\n", "Room Rate ID", "Room Rate Name", "Rate Per Night", "Start Date", "End Date");
                System.out.printf("%19s%18s%17s%15s%31s\n", promotionRate.getRoomRateId(), promotionRate.getName(), promotionRate.getRatePerNight(), promotionRate.getStartDate(), promotionRate.getEndDate());
            } else {
                System.out.printf("%19s%18s%17s%\n", "Room Rate ID", "Room Rate Name", "Rate Per Night");
                System.out.printf("%19s%18s%17s%15s%31s\n", roomRate.getRoomRateId(), roomRate.getName(), roomRate.getRatePerNight());
            }
            System.out.print("Press any key to continue...> ");
            scanner.nextLine();
        } catch (RoomRateNotFoundException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
    
    public void doViewAllRoomRates() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** HoRS System :: View All Room Rate Details ***\n");
        List<RoomRateEntity> roomRates = roomRateSessionBean.retrieveAllRoomRates();
        for(RoomRateEntity roomRate: roomRates) {
            if(roomRate instanceof PeakRateEntity) {
                PeakRateEntity peakRate = (PeakRateEntity) roomRate;
                System.out.printf("%19s%18s%17s%15s%31s\n", "Room Rate ID", "Room Rate Name", "Rate Per Night", "Start Date", "End Date");
                System.out.printf("%19s%18s%17s%15s%31s\n", peakRate.getRoomRateId(), peakRate.getName(), peakRate.getRatePerNight(), peakRate.getStartDate(), peakRate.getEndDate());
            } else if(roomRate instanceof PromotionRateEntity) {
                PromotionRateEntity promotionRate = (PromotionRateEntity) roomRate;
                System.out.printf("%19s%18s%17s%15s%31s\n", "Room Rate ID", "Room Rate Name", "Rate Per Night", "Start Date", "End Date");
                System.out.printf("%19s%18s%17s%15s%31s\n", promotionRate.getRoomRateId(), promotionRate.getName(), promotionRate.getRatePerNight(), promotionRate.getStartDate(), promotionRate.getEndDate());
            } else {
                System.out.printf("%19s%18s%17s%\n", "Room Rate ID", "Room Rate Name", "Rate Per Night");
                System.out.printf("%19s%18s%17s%\n", roomRate.getRoomRateId(), roomRate.getName(), roomRate.getRatePerNight());
            }
        }

        System.out.println("1: Update Product");
        System.out.println("2: Delete Product");
        System.out.println("3: Back\n");
        System.out.print("> ");
        int response = sc.nextInt();

        if (response == 1) {
            doUpdateRoomRate();
        } else if (response == 2) {
            doDeleteRoomRate();
        }
    }

    private void doUpdateRoomRate()
    {
        try {
            Scanner scanner = new Scanner(System.in);
            String input;
            
            System.out.println("*** HoRS System :: View ALl Room Rate Details :: Update Room Rate ***\n");
            System.out.print("Enter Name of Room Rate> ");
            String roomRateName = scanner.nextLine().trim();
            RoomRateEntity roomRate = roomRateSessionBean.retrieveRoomRateByRoomRateName(roomRateName);
            
            System.out.print("Enter Rate Per Night (-1 if no change)> ");
            Double rate = scanner.nextDouble();
            if(rate > 0)
            {
                BigDecimal changedRate = BigDecimal.valueOf(rate);
                roomRate.setRatePerNight(changedRate);
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            if (roomRate instanceof PromotionRateEntity) {
                System.out.print("Enter Start Date of Validity Period [yyyy-MM-dd HH:mm] (blank if no change)> ");
                String startDate = scanner.nextLine();
                if(startDate.length()>0) {
                    LocalDateTime startDateTime = LocalDateTime.parse(startDate, formatter);
                    ((PromotionRateEntity) roomRate).setStartDate(startDateTime);
                } 
                System.out.print("Enter End Date of Validity Period [yyyy-MM-dd HH:mm] (blank if no change)> ");
                String endDate = scanner.nextLine();
                if(endDate.length()>0) {
                    LocalDateTime endDateTime = LocalDateTime.parse(endDate, formatter);
                    ((PromotionRateEntity) roomRate).setStartDate(endDateTime);
                }
            } else if (roomRate instanceof PeakRateEntity) {
                System.out.print("Enter Start Date of Validity Period [yyyy-MM-dd HH:mm] (blank if no change)> ");
                String startDate = scanner.nextLine();
                if(startDate.length()>0) {
                    LocalDateTime startDateTime = LocalDateTime.parse(startDate, formatter);
                    ((PeakRateEntity) roomRate).setStartDate(startDateTime);
                } 
                System.out.print("Enter End Date of Validity Period [yyyy-MM-dd HH:mm] (blank if no change)> ");
                String endDate = scanner.nextLine();
                if(endDate.length()>0) {
                    LocalDateTime endDateTime = LocalDateTime.parse(endDate, formatter);
                    ((PeakRateEntity) roomRate).setStartDate(endDateTime);
                }
            }

            Set<ConstraintViolation<RoomRateEntity>>constraintViolations = validator.validate(roomRate);
            
            if(constraintViolations.isEmpty())
            {
                try
                {
                    roomRateSessionBean.updateRoomRate(roomRate);
                    System.out.println("RoomRate updated successfully!\n");
                }
                
                catch(InputDataValidationException ex)
                {
                    System.out.println(ex.getMessage() + "\n");
                } catch (UpdateRoomRateException ex) {
                    System.out.println(ex.getMessage() + "\n");
                }
            }
            else
            {
                showInputDataValidationErrorsForRoomRateEntity(constraintViolations);
            }
        } catch (RoomRateNotFoundException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
    
    private void showInputDataValidationErrorsForRoomRateEntity(Set<ConstraintViolation<RoomRateEntity>>constraintViolations)
    {
        System.out.println("\nInput data validation error!:");
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }
    
    
    
    // Updated in v4.1
    
    private void doDeleteRoomRate()
    {
        try {
            Scanner scanner = new Scanner(System.in);
            String input;
            
            System.out.println("*** HoRS System :: View ALl Room Rate Details :: Delete Room Rate ***\n");
            System.out.print("Enter Name of Room Rate> ");
            String roomRateName = scanner.nextLine().trim();
            RoomRateEntity roomRate = roomRateSessionBean.retrieveRoomRateByRoomRateName(roomRateName);
            
            System.out.printf("Confirm Delete Room Rate %s (Room Rate ID: %d) (Enter 'Y' to Delete)> ", roomRate.getName(), roomRate.getRoomRateId());
            input = scanner.nextLine().trim();
            
            if (input.equals("Y"))
            {
                try {
                    roomRateSessionBean.deleteRoomRate(roomRate.getRoomRateId());
                    System.out.println("Room Rate successfully deleted!");
                } catch (RoomRateNotFoundException ex) {
                    System.out.println("Error: " + ex.getMessage());
                } catch (DeleteRoomRateException ex) {
                    System.out.println("Error: " + ex.getMessage());
                }
            }
            else
            {
                System.out.println("Room Rate NOT deleted!\n");
            }
        } catch (RoomRateNotFoundException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}
