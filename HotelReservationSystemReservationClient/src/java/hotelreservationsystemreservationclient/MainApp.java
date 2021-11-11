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
import entity.ReservationEntity;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.exception.GuestEmailExistException;
import util.exception.GuestNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.ReservationNotFoundException;
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
    }

    public void runApp() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        if(currentCustomerEntity != null) {
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
                            Long customerId = doRegister();
                            currentCustomerEntity = (CustomerEntity) guestSessionBean.retrieveGuestByGuestId(customerId);
                            registeredCustomerMenu();
                        } catch (GuestEmailExistException | UnknownPersistenceException | GuestNotFoundException | InputDataValidationException ex) {
                            System.out.println("Error: " + ex.getMessage());
                        }
                    } else if (response == 3) {
                        //searchHotelRoom();
                    } else if (response == 4) {
                        break;
                    }else {
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
    
    private Long doRegister() throws GuestEmailExistException, UnknownPersistenceException, InputDataValidationException {
        Scanner scanner = new Scanner(System.in);
        String name = "";
        String email = "";
        String phoneNo = "";
        String password = "";

        System.out.println("*** HoRS System :: Register as Guest ***\n");
        System.out.print("Enter email> ");
        email = scanner.nextLine().trim();
        System.out.print("Enter name> ");
        name = scanner.nextLine().trim();
        System.out.print("Enter phoneNo> ");
        phoneNo = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();
        return guestSessionBean.createNewGuest(new CustomerEntity(name, email, phoneNo, password));  
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
                    //searchHotelRoom();
                    int nextResponse = 0;
                    while (true) {
                        System.out.println("*** Hotel Reservation (HoR) System ***\n");
                        System.out.println("1: Reserve Hotel Room");
                        System.out.println("2: Back");
                        while (nextResponse < 1 || nextResponse > 2) {
                            System.out.print("> ");

                            response = scanner.nextInt();

                            if (nextResponse == 1) {
                                //reserveHotelRoom();
                            } else if (nextResponse == 2) {
                                break;
                            } else {
                                System.out.println("Invalid option, please try again!\n");
                            }
                        }
                        if (nextResponse == 2) {
                            break;
                        }
                    }
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
    
    public void viewAllReservations() {
        try {
            System.out.println("*** Hotel Reservation (HoR) System :: View All Reservations ***\n");
            List<ReservationEntity> reservations = guestSessionBean.retrieveGuestByGuestId(currentCustomerEntity.getGuestId()).getReservations();
            System.out.printf("%20s%20s%20s%20s%20s%20s\n", "Reservation ID", "Number of Rooms", "Room Type", "Reservation Fee", "Start Date", "End Date");
            for(ReservationEntity reservation: reservations) {
                System.out.printf("%20s%20s%20s%20s%30s%30s\n", reservation.getReservationId(), reservation.getNumberOfRooms(), reservation.getRoomType(), reservation.getReservationFee(), reservation.getStartDate(), reservation.getEndDate());
            }
        } catch (GuestNotFoundException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
    
    public void viewReservationDetails() {
        try {
            Scanner sc = new Scanner(System.in);
            System.out.println("*** Hotel Reservation (HoR) System :: View Reservation Details ***\n");
            System.out.println("Enter Reservation ID >");
            Long reservationId = sc.nextLong();
            ReservationEntity reservation = reservationSessionBean.retrieveReservationByReservationId(reservationId);
            System.out.printf("%20s%20s%20s%20s%20s%20s\n", "Reservation ID", "Number of Rooms", "Room Type", "Reservation Fee", "Start Date", "End Date");
            System.out.printf("%20s%20s%20s%20s%30s%30s\n", reservation.getReservationId(), reservation.getNumberOfRooms(), reservation.getRoomType(), reservation.getReservationFee(), reservation.getStartDate(), reservation.getEndDate());
        } catch (ReservationNotFoundException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        
    }

}
