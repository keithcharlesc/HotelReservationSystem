package hotelreservationsystemmanagementclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.PartnerEmployeeSessionBeanRemote;
import entity.EmployeeEntity;
import java.util.List;
import java.util.Scanner;
import util.enumeration.EmployeeAccessRightEnum;
import util.exception.EmployeeUsernameExistException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;


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
            System.out.println("*** HoRS System :: System Administration ***\n");
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
    
    public void doCreateNewEmployee() {
        try {
            Scanner scanner = new Scanner(System.in);
            String username = "";
            String password = "";
            
            EmployeeEntity newEmployee = new EmployeeEntity();
            
            System.out.println("*** HoRS System :: Login ***\n");
            System.out.print("Enter username> ");
            username = scanner.nextLine().trim();
            newEmployee.setUsername(username);
            System.out.print("Enter password> ");
            password = scanner.nextLine().trim();
            newEmployee.setPassword(password);
            
            while(true)
            {
                System.out.print("Select Employee Access Right (1: SYSTEM ADMINISTRATOR 2: OPERATION MANAGER 3: SALES MANAGER 4: GUEST RELATION OFFICER)> ");
                Integer accessRightInt = scanner.nextInt();
                
                if(accessRightInt >= 1 && accessRightInt <= 4)
                {
                    newEmployee.setEmployeeAccessRightEnum(EmployeeAccessRightEnum.values()[accessRightInt-1]);
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");
                }
            }
            employeeSessionBean.createNewEmployee(newEmployee);
        } catch (EmployeeUsernameExistException ex) {
            System.out.println("Error: " + ex.getMessage());
        } catch (UnknownPersistenceException ex) {
            System.out.println("Error: " + ex.getMessage());
        } catch (InputDataValidationException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
    
    public void doViewAllEmployees() {
        List<EmployeeEntity> employees = employeeSessionBean.retrieveAllEmployees();
        for(EmployeeEntity employee: employees) {
            System.out.println();
        }
    }
}
