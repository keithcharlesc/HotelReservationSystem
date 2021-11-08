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
import java.util.Scanner;


public class HotelOperationModule {
    private RoomTypeSessionBeanRemote roomTypeSessionBean;
    private RoomSessionBeanRemote roomSessionBean;
    private RoomRateSessionBeanRemote roomRateSessionBean;
    private ExceptionRecordSessionBeanRemote exceptionRecordSessionBean;
    private EmployeeEntity curremtEmployee;

    public HotelOperationModule() {
    }

    public HotelOperationModule(RoomTypeSessionBeanRemote roomTypeSessionBean, RoomSessionBeanRemote roomSessionBean, RoomRateSessionBeanRemote roomRateSessionBean, ExceptionRecordSessionBeanRemote exceptionRecordSessionBean, EmployeeEntity curremtEmployee) {
        this.roomTypeSessionBean = roomTypeSessionBean;
        this.roomSessionBean = roomSessionBean;
        this.roomRateSessionBean = roomRateSessionBean;
        this.exceptionRecordSessionBean = exceptionRecordSessionBean;
        this.curremtEmployee = curremtEmployee;
    }
    
    public void operationManagerOperations() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** POS System :: Cashier Operation ***\n");
            System.out.println("1: Checkout");
            System.out.println("2: Void/Refund");
            System.out.println("3: View My Sale Transactions");
            System.out.println("4: Back\n");
            response = 0;
            
            while(response < 1 || response > 3)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    doCheckout();
                }
                else if(response == 2)
                {
                    doVoidRefund();
                }
                else if(response == 3)
                {
                    doViewMySaleTransactions();
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
    
    public void salesManagerOperations() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** POS System :: Operation Manager Operation ***\n");
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
            
            while(response < 1 || response > 9)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    doCreateNewRoomType();
                }
                else if(response == 2)
                {
                    doViewRoomTypeDetails();
                }
                else if(response == 3)
                {
                    doViewMySaleTransactions();
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
