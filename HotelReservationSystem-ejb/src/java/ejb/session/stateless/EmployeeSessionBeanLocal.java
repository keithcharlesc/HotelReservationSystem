/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.EmployeeEntity;
import java.util.List;
import javax.ejb.Local;
import util.exception.EmployeeUsernameExistException;
import util.exception.EmployeeNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author keithcharleschan
 */
@Local
public interface EmployeeSessionBeanLocal {
    
    public EmployeeEntity createNewEmployee(EmployeeEntity newEmployeeEntity) throws EmployeeUsernameExistException, UnknownPersistenceException;
    public List<EmployeeEntity> retrieveAllEmployees();
    public EmployeeEntity retrieveEmployeeByEmployeeId(Long employeeId) throws EmployeeNotFoundException;
    public EmployeeEntity retrieveEmployeeByEmployeeUsername(String username) throws EmployeeNotFoundException;
    public void deleteEmployee(Long employeeId) throws EmployeeNotFoundException;

    
}
