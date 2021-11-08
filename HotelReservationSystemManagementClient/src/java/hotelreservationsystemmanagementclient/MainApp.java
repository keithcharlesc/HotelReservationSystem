package hotelreservationsystemmanagementclient;

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
import entity.EmployeeEntity;
import java.util.Scanner;
import util.exception.InvalidLoginCredentialException;

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
    private static GuestSessionBeanRemote guestSessionBean;

    private EmployeeEntity currentEmployeeEntity;
    
    private SystemAdministrationModule systemAdministrationModule;
    private HotelOperationModule hotelOperationModule;
    private FrontOfficeModule frontOfficeModule;
    
    
    
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
        
        while(true)
        {
            System.out.println("*** Welcome to Hotel Reservation (HoR) System ***\n");
            System.out.println("1: Login");
            System.out.println("2: Exit\n");
            response = 0;
            
            while(response < 1 || response > 2)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    try
                    {
                        doLogin();
                        System.out.println("Login successful!\n");
                        
                        systemAdministrationModule = new SystemAdministrationModule(employeeSessionBean, partnerEmployeeSessionBean, currentEmployeeEntity);
                        hotelOperationModule = new HotelOperationModule(roomTypeSessionBean, roomSessionBean, roomRateSessionBean, exceptionRecordSessionBean, currentEmployeeEntity);
                        frontOfficeModule = new FrontOfficeModule(roomSessionBean, saleTransactionEntitySessionBeanRemote, checkoutBeanRemote, emailSessionBeanRemote, queueCheckoutNotification, queueCheckoutNotificationFactory, currentEmployeeEntity);

                        menuMain();
                    }
                    catch(InvalidLoginCredentialException ex) 
                    {
                        System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                    }
                }
                else if (response == 2)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 2)
            {
                break;
            }
        }
    }
    
    private void doLogin() throws InvalidLoginCredentialException
    {
        Scanner scanner = new Scanner(System.in);
        String username = "";
        String password = "";
        
        System.out.println("*** HoRS System :: Login ***\n");
        System.out.print("Enter username> ");
        username = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();
        
        if(username.length() > 0 && password.length() > 0)
        {
            currentEmployeeEntity = employeeSessionBean.employeeLogin(username, password);
        }
        else
        {
            throw new InvalidLoginCredentialException("Missing login credential!");
        }
    }
    
}
