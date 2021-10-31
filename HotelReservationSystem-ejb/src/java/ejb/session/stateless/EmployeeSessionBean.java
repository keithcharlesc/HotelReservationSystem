package ejb.session.stateless;

import entity.EmployeeEntity;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.DeleteEmployeeException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.EmployeeNotFoundException;
import util.exception.EmployeeUsernameExistException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateEmployeeException;

@Stateless

public class EmployeeSessionBean implements EmployeeSessionBeanLocal, EmployeeSessionBeanRemote {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    // Added in v4.2 for bean validation
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public EmployeeSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    // Updated in v4.1
    // Updated in v4.2 with bean validation
    @Override
    public Long createNewEmployee(EmployeeEntity newEmployeeEntity) throws EmployeeUsernameExistException, UnknownPersistenceException, InputDataValidationException {
        Set<ConstraintViolation<EmployeeEntity>> constraintViolations = validator.validate(newEmployeeEntity);

        if (constraintViolations.isEmpty()) {
            try {
                em.persist(newEmployeeEntity);
                em.flush();

                return newEmployeeEntity.getEmployeeId();
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new EmployeeUsernameExistException();
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    @Override
    public List<EmployeeEntity> retrieveAllEmployees() {
        Query query = em.createQuery("SELECT s FROM EmployeeEntity s");

        return query.getResultList();
    }

    @Override
    public EmployeeEntity retrieveEmployeeByEmployeeId(Long employeeId) throws EmployeeNotFoundException {
        EmployeeEntity employeeEntity = em.find(EmployeeEntity.class, employeeId);

        if (employeeEntity != null) {
            return employeeEntity;
        } else {
            throw new EmployeeNotFoundException("Employee ID " + employeeId + " does not exist!");
        }
    }

    @Override
    public EmployeeEntity retrieveEmployeeByUsername(String username) throws EmployeeNotFoundException {
        Query query = em.createQuery("SELECT s FROM EmployeeEntity s WHERE s.username = :inUsername");
        query.setParameter("inUsername", username);

        try {
            return (EmployeeEntity) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new EmployeeNotFoundException("Employee Username " + username + " does not exist!");
        }
    }

    @Override
    public EmployeeEntity employeeLogin(String username, String password) throws InvalidLoginCredentialException {
        try {
            EmployeeEntity employeeEntity = retrieveEmployeeByUsername(username);

            if (employeeEntity.getPassword().equals(password)) {
                return employeeEntity;
            } else {
                throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
            }
        } catch (EmployeeNotFoundException ex) {
            throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
        }
    }

    // Updated in v4.1 to update selective attributes instead of merging the entire state passed in from the client
    // Also check for existing employee before proceeding with the update
    // Updated in v4.2 with bean validation
    @Override
    public void updateEmployee(EmployeeEntity employeeEntity) throws EmployeeNotFoundException, UpdateEmployeeException, InputDataValidationException {
        if (employeeEntity != null && employeeEntity.getEmployeeId() != null) {
            Set<ConstraintViolation<EmployeeEntity>> constraintViolations = validator.validate(employeeEntity);

            if (constraintViolations.isEmpty()) {
                EmployeeEntity employeeEntityToUpdate = retrieveEmployeeByEmployeeId(employeeEntity.getEmployeeId());

                if (employeeEntityToUpdate.getUsername().equals(employeeEntity.getUsername())) {
                    employeeEntityToUpdate.setFirstName(employeeEntity.getFirstName());
                    employeeEntityToUpdate.setLastName(employeeEntity.getLastName());
                    employeeEntityToUpdate.setEmployeeAccessRightEnum(employeeEntity.getEmployeeAccessRightEnum());
                    // Username and password are deliberately NOT updated to demonstrate that client is not allowed to update account credential through this business method
                } else {
                    throw new UpdateEmployeeException("Username of employee record to be updated does not match the existing record");
                }
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } else {
            throw new EmployeeNotFoundException("Employee ID not provided for employee to be updated");
        }
    }

    // Updated in v4.1
    @Override
    public void deleteEmployee(Long employeeId) throws EmployeeNotFoundException, DeleteEmployeeException {
        EmployeeEntity employeeEntityToRemove = retrieveEmployeeByEmployeeId(employeeId);

//        if (employeeEntityToRemove.getSaleTransactionEntities().isEmpty()) {
            em.remove(employeeEntityToRemove);
//        } else {
            // New in v4.1 to prevent deleting employee with existing sale transaction(s)
//            throw new DeleteEmployeeException("Employee ID " + employeeId + " is associated with existing sale transaction(s) and cannot be deleted!");
//        }
    }

    // Added in v4.2
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<EmployeeEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
