package hotelreservationsystemmanagementclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.PartnerEmployeeSessionBeanRemote;
import entity.EmployeeEntity;
import entity.PartnerEmployeeEntity;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.EmployeeAccessRightEnum;
import util.exception.EmployeeUsernameExistException;
import util.exception.InputDataValidationException;
import util.exception.InvalidAccessRightException;
import util.exception.PartnerUsernameExistException;
import util.exception.UnknownPersistenceException;

public class SystemAdministrationModule {

    private EmployeeSessionBeanRemote employeeSessionBean;
    private PartnerEmployeeSessionBeanRemote partnerEmployeeSessionBean;
    private EmployeeEntity currentEmployee;
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public SystemAdministrationModule() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public SystemAdministrationModule(EmployeeSessionBeanRemote employeeSessionBean, PartnerEmployeeSessionBeanRemote partnerEmployeeSessionBean, EmployeeEntity currentEmployee) {
        this();
        this.employeeSessionBean = employeeSessionBean;
        this.partnerEmployeeSessionBean = partnerEmployeeSessionBean;
        this.currentEmployee = currentEmployee;
    }

    public void menuSystemAdministration() throws InvalidAccessRightException {

        if (currentEmployee.getEmployeeAccessRightEnum() != EmployeeAccessRightEnum.SYSTEM_ADMINISTRATOR) {
            throw new InvalidAccessRightException("You don't have SYSTEM ADMINISTRATOR rights to access the system administration module.");
        }

        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** HoRS System :: System Administration ***\n");
            System.out.println("1: Create New Employee");
            System.out.println("2: View All Employee");
            System.out.println("3: Create New Partner");
            System.out.println("4: View All Partners");
            System.out.println("5: Back\n");
            response = 0;

            while (response < 1 || response > 5) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    doCreateNewEmployee();
                } else if (response == 2) {
                    doViewAllEmployees();
                } else if (response == 3) {
                    doCreateNewPartner();
                } else if (response == 4) {
                    doViewAllPartners();
                } else if (response == 5) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 5) {
                break;
            }
        }
    }

    public void doCreateNewEmployee() {
        Scanner scanner = new Scanner(System.in);
        String username = "";
        String password = "";

        EmployeeEntity newEmployee = new EmployeeEntity();

        System.out.println("*** HoRS System :: Create New Employee ***\n");
        System.out.print("Enter username> ");
        username = scanner.nextLine().trim();
        newEmployee.setUsername(username);
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();
        newEmployee.setPassword(password);

        while (true) {
            System.out.print("Select Employee Access Right (1: SYSTEM ADMINISTRATOR 2: OPERATION MANAGER 3: SALES MANAGER 4: GUEST RELATION OFFICER)> ");
            Integer accessRightInt = scanner.nextInt();

            if (accessRightInt >= 1 && accessRightInt <= 4) {
                newEmployee.setEmployeeAccessRightEnum(EmployeeAccessRightEnum.values()[accessRightInt - 1]);
                break;
            } else {
                System.out.println("Invalid option, please try again!\n");
            }
        }
        Set<ConstraintViolation<EmployeeEntity>> constraintViolations = validator.validate(newEmployee);

        if (constraintViolations.isEmpty()) {
            try {
                Long newEmployeeId = employeeSessionBean.createNewEmployee(newEmployee);
                System.out.println("New employee created successfully!: " + newEmployeeId + "\n");
            } catch (EmployeeUsernameExistException ex) {
                System.out.println("An error has occurred while creating the new employee!: The user name already exist\n");
            } catch (UnknownPersistenceException ex) {
                System.out.println("An unknown error has occurred while creating the new employee!: " + ex.getMessage() + "\n");
            } catch (InputDataValidationException ex) {
                System.out.println(ex.getMessage() + "\n");
            }
        } else {
            showInputDataValidationErrorsForEmployeeEntity(constraintViolations);
        }

    }

    private void doViewAllEmployees() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("*** HoRS System :: System Administration :: View All Employees ***\n");
        List<EmployeeEntity> employeeEntities = employeeSessionBean.retrieveAllEmployees();
        System.out.printf("%20s%30s%20s%20s\n", "Employee ID", "Employee Access Right", "Username", "Password");
        for (EmployeeEntity employeeEntity : employeeEntities) {
            System.out.printf("%20s%30s%20s%20s\n", employeeEntity.getEmployeeId().toString(), employeeEntity.getEmployeeAccessRightEnum().toString(), employeeEntity.getUsername(), employeeEntity.getPassword());
        }
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }

    private void showInputDataValidationErrorsForEmployeeEntity(Set<ConstraintViolation<EmployeeEntity>> constraintViolations) {
        System.out.println("\nInput data validation error!:");

        for (ConstraintViolation constraintViolation : constraintViolations) {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }

    public void doCreateNewPartner() {
        Scanner scanner = new Scanner(System.in);
        String name = "";
        String username = "";
        String password = "";

        PartnerEmployeeEntity newPartner = new PartnerEmployeeEntity();

        System.out.println("*** HoRS System :: Create New Partner ***\n");
        System.out.print("Enter name> ");
        name = scanner.nextLine().trim();
        newPartner.setName(name);
        System.out.print("Enter username> ");
        username = scanner.nextLine().trim();
        newPartner.setUsername(username);
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();
        newPartner.setPassword(password);

        Set<ConstraintViolation<PartnerEmployeeEntity>> constraintViolations = validator.validate(newPartner);

        if (constraintViolations.isEmpty()) {
            try {
                Long newParterId = partnerEmployeeSessionBean.createNewPartnerEmployee(newPartner);
                System.out.println("New partner created successfully!: " + newParterId + "\n");
            } catch (PartnerUsernameExistException ex) {
                System.out.println("An error has occurred while creating the new employee!: The user name already exist\n");
            } catch (UnknownPersistenceException ex) {
                System.out.println("An unknown error has occurred while creating the new employee!: " + ex.getMessage() + "\n");
            } catch (InputDataValidationException ex) {
                System.out.println(ex.getMessage() + "\n");
            }
        } else {
            showInputDataValidationErrorsForPartnerEmployeeEntity(constraintViolations);
        }
    }

    private void doViewAllPartners() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("*** HoRS System :: System Administration :: View All Partners ***\n");
        List<PartnerEmployeeEntity> partnerEntities = partnerEmployeeSessionBean.retrieveAllPartnerEmployees();
        System.out.printf("%20s%30s%20s%20s\n", "Partner ID", "Name", "Username", "Password");
        for (PartnerEmployeeEntity partnerEntity : partnerEntities) {
            System.out.printf("%20s%30s%20s%20s\n", partnerEntity.getPartnerId().toString(), partnerEntity.getName(), partnerEntity.getUsername(), partnerEntity.getPassword());
        }
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }

    private void showInputDataValidationErrorsForPartnerEmployeeEntity(Set<ConstraintViolation<PartnerEmployeeEntity>> constraintViolations) {
        System.out.println("\nInput data validation error!:");

        for (ConstraintViolation constraintViolation : constraintViolations) {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }

}
