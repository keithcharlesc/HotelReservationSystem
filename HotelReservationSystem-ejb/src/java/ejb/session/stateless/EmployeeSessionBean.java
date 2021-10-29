package ejb.session.stateless;

import entity.EmployeeEntity;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.EmployeeExistException;
import util.exception.EmployeeNotFoundException;
import util.exception.UnknownPersistenceException;

@Stateless
public class EmployeeSessionBean implements EmployeeSessionBeanRemote, EmployeeSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager entityManager;

    public EmployeeSessionBean()
    {
    }
    
    @Override
    public EmployeeEntity createNewEmployee(EmployeeEntity newEmployeeEntity) throws EmployeeExistException, UnknownPersistenceException
    {
        try
        {
            entityManager.persist(newEmployeeEntity);
            entityManager.flush();

            return newEmployeeEntity;
        }
        catch(PersistenceException ex)
        {
            if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
            {
                if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                {
                    throw new EmployeeExistException("Employee already exist");
                }
                else
                {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            }
            else
            {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
    }
    
   
    @Override
    public List<EmployeeEntity> retrieveAllEmployees()
    {
        Query query = entityManager.createQuery("SELECT e FROM EmployeeEntity e ORDER BY e.employeeId");
        
        return query.getResultList();
    }
    
    
    
    @Override
    public EmployeeEntity retrieveEmployeeByEmployeeId(Long employeeId) throws EmployeeNotFoundException
    {
        EmployeeEntity employeeEntity = entityManager.find(EmployeeEntity.class, employeeId);
        
        if(employeeEntity != null)
        {
            return employeeEntity;
        }
        else
        {
            throw new EmployeeNotFoundException("Employee ID " + employeeId + " does not exist!");
        }               
    }
    
    
    
    @Override
    public EmployeeEntity retrieveEmployeeByEmployeeUsername(String username) throws EmployeeNotFoundException
    {
        Query query = entityManager.createQuery("SELECT e FROM EmployeeEntity e WHERE e.username = :inUsername");
        query.setParameter("inUsername", username);
        
        try
        {
            return (EmployeeEntity)query.getSingleResult();
        }
        catch(NoResultException | NonUniqueResultException ex)
        {
            throw new EmployeeNotFoundException("Username " + username + " does not exist!");
        }
    }

    @Override
    public void deleteEmployee(Long employeeId) throws EmployeeNotFoundException
    {
        EmployeeEntity employeeEntityToRemove = retrieveEmployeeByEmployeeId(employeeId);

        if(employeeEntityToRemove != null)
        {
            entityManager.remove(employeeEntityToRemove);
        }
        else
        {
            throw new EmployeeNotFoundException("Employee ID " + employeeId + " is not found");
        }
    }
 
}
