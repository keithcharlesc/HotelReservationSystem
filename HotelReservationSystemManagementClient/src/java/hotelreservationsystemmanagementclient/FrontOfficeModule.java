package hotelreservationsystemmanagementclient;

import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import entity.EmployeeEntity;
import java.util.Scanner;


public class FrontOfficeModule {
    private RoomSessionBeanRemote roomSessionBean;
    private GuestSessionBeanRemote guestSessionBean;
    private ReservationSessionBeanRemote reservationSessionBean;
    private EmployeeEntity currentEmployee;

    public FrontOfficeModule() {
    }

    public FrontOfficeModule(RoomSessionBeanRemote roomSessionBean, GuestSessionBeanRemote guestSessionBean, ReservationSessionBeanRemote reservationSessionBean, EmployeeEntity currentEmployee) {
        this.roomSessionBean = roomSessionBean;
        this.guestSessionBean = guestSessionBean;
        this.reservationSessionBean = reservationSessionBean;
        this.currentEmployee = currentEmployee;
    }
    
    public void guestRelationOfficerOperations() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** POS System :: Guest Relation Officer Operation ***\n");
            System.out.println("1: Walk-In Search Room"); //includes walk-in reserve room
            System.out.println("2: Check-in Guest");
            System.out.println("3: Check-out Guest");
            System.out.println("4: Logout\n");
            response = 0;
            
            while(response < 1 || response > 3)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    doWalkInSearchRoom();
                }
                else if(response == 2)
                {
                    doCheckInGuest();
                }
                else if(response == 3)
                {
                    doCheckOutGuest();
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
}
