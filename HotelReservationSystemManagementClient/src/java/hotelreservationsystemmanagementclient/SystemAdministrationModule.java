package hotelreservationsystemmanagementclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.PartnerEmployeeSessionBeanRemote;
import entity.EmployeeEntity;
import java.util.Scanner;


public class SystemAdministrationModule {
    private EmployeeSessionBeanRemote employeeSessionBean;
    private PartnerEmployeeSessionBeanRemote partnerEmployeeSessionBean;
    private EmployeeEntity currentEmployee;

    public SystemAdministrationModule() {
    }

    public SystemAdministrationModule(EmployeeSessionBeanRemote employeeSessionBean, PartnerEmployeeSessionBeanRemote partnerEmployeeSessionBean, EmployeeEntity currentEmployee) {
        this.employeeSessionBean = employeeSessionBean;
        this.partnerEmployeeSessionBean = partnerEmployeeSessionBean;
        this.currentEmployee = currentEmployee;
    }
    
    public void systemAdministratorOperations() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** POS System :: System Administration ***\n");
            System.out.println("1: Create New Employee");
            System.out.println("2: View All Employee");
            System.out.println("3: Create New Partner");
            System.out.println("4: View All Partners");
            System.out.println("5: Back\n");
            response = 0;
            
            while(response < 1 || response > 5)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    doCreateNewEmployee();
                }
                else if(response == 2)
                {
                    doViewAllEmployees();
                }
                else if(response == 3)
                {
                    doCreateNewPartner();
                }
                else if(response == 4)
                {
                    doViewAllPartners();
                }
                else if(response == 5)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 5)
            {
                break;
            }
        }
    }
}
