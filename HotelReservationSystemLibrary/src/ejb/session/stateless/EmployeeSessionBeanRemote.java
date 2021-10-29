package ejb.session.stateless;

import entity.EmployeeEntity;
import java.util.List;
import javax.ejb.Remote;
import util.exception.DeleteEmployeeException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.EmployeeNotFoundException;
import util.exception.EmployeeUsernameExistException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateEmployeeException;

@Remote
public interface EmployeeSessionBeanRemote {

    Long createNewEmployee(EmployeeEntity newEmployeeEntity) throws EmployeeUsernameExistException, UnknownPersistenceException, InputDataValidationException;

    List<EmployeeEntity> retrieveAllEmployees();

    EmployeeEntity retrieveEmployeeByEmployeeId(Long employeeId) throws EmployeeNotFoundException;

    EmployeeEntity retrieveEmployeeByUsername(String username) throws EmployeeNotFoundException;

    EmployeeEntity employeeLogin(String username, String password) throws InvalidLoginCredentialException;

    void updateEmployee(EmployeeEntity employeeEntity) throws EmployeeNotFoundException, UpdateEmployeeException, InputDataValidationException;

    void deleteEmployee(Long employeeId) throws EmployeeNotFoundException, DeleteEmployeeException;
}
