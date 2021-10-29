/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.EmployeeEntity;
import java.util.List;
import javax.ejb.Remote;
import util.exception.EmployeeExistException;
import util.exception.EmployeeNotFoundException;
import util.exception.UnknownPersistenceException;


@Remote
public interface EmployeeSessionBeanRemote {
    
    public EmployeeEntity createNewEmployee(EmployeeEntity newEmployeeEntity) throws EmployeeExistException, UnknownPersistenceException;
    public List<EmployeeEntity> retrieveAllEmployees();
    public EmployeeEntity retrieveEmployeeByEmployeeId(Long employeeId) throws EmployeeNotFoundException;
    public EmployeeEntity retrieveEmployeeByEmployeeUsername(String username) throws EmployeeNotFoundException;
    public void deleteEmployee(Long employeeId) throws EmployeeNotFoundException;
    
}
