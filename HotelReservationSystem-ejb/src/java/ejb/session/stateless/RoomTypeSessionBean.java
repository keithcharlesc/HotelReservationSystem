/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomTypeEntity;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
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
import util.exception.DeleteRoomTypeException;
import util.exception.InputDataValidationException;
import util.exception.RoomTypeNameExistException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomTypeException;

/**
 *
 * @author xianhui
 */
@Stateless
public class RoomTypeSessionBean implements RoomTypeSessionBeanRemote, RoomTypeSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager entityManager;
    
    //need change this and have more 
    @EJB
    private RoomRateSessionBeanLocal roomRateSessionBeanLocal;
    private RoomSessionBeanLocal roomSessionBeanLocal;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    
    
    public RoomTypeSessionBean()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    
    @Override
    public RoomTypeEntity createNewRoomType(RoomTypeEntity newRoomTypeEntity) throws RoomTypeNameExistException, UnknownPersistenceException, InputDataValidationException
    {
        Set<ConstraintViolation<RoomTypeEntity>>constraintViolations = validator.validate(newRoomTypeEntity);
        
        if(constraintViolations.isEmpty())
        {
            try
            {
                entityManager.persist(newRoomTypeEntity);
                entityManager.flush();

                return newRoomTypeEntity;
            }
            catch(PersistenceException ex)
            {
                if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
                {
                    if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                    {
                        throw new RoomTypeNameExistException();
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
        else
        {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    
    
    @Override
    public List<RoomTypeEntity> retrieveAllRoomTypes()
    {
        Query query = entityManager.createQuery("SELECT r FROM RoomTypeEntity r ORDER BY r.roomTypeName");
        
        return query.getResultList();
    }
    
    
    
    @Override
    public RoomTypeEntity retrieveRoomTypeByRoomTypeId(Long roomTypeId) throws RoomTypeNotFoundException
    {
        RoomTypeEntity roomTypeEntity = entityManager.find(RoomTypeEntity.class, roomTypeId);
        
        if(roomTypeEntity != null)
        {
            roomTypeEntity.getRooms().size();
            roomTypeEntity.getRoomRates().size();
            return roomTypeEntity;
        }
        else
        {
            throw new RoomTypeNotFoundException("RoomType ID " + roomTypeId + " does not exist!");
        }               
    }
    
    
    
    @Override
    public RoomTypeEntity retrieveRoomTypeByRoomTypeRoomTypeName(String name) throws RoomTypeNotFoundException
    {
        Query query = entityManager.createQuery("SELECT p FROM RoomTypeEntity p WHERE p.skuCode = :inRoomTypeName");
        query.setParameter("inRoomTypeName", name);
        
        try
        {
            RoomTypeEntity roomTypeEntity = (RoomTypeEntity)query.getSingleResult();
            roomTypeEntity.getRooms().size();
            roomTypeEntity.getRoomRates().size();
            return roomTypeEntity;
        }
        catch(NoResultException | NonUniqueResultException ex)
        {
            throw new RoomTypeNotFoundException("Room Type Name " + name + " does not exist!");
        }
    }
    

    @Override
    public void updateRoomType(RoomTypeEntity roomTypeEntity) throws RoomTypeNotFoundException, UpdateRoomTypeException, InputDataValidationException
    {
        if(roomTypeEntity != null && roomTypeEntity.getRoomTypeId()!= null)
        {
            Set<ConstraintViolation<RoomTypeEntity>>constraintViolations = validator.validate(roomTypeEntity);
        
            if(constraintViolations.isEmpty())
            {
                RoomTypeEntity roomTypeEntityToUpdate = retrieveRoomTypeByRoomTypeId(roomTypeEntity.getRoomTypeId());

                if(roomTypeEntityToUpdate.getRoomTypeName().equals(roomTypeEntity.getRoomTypeName()))
                {
                    roomTypeEntityToUpdate.setIsDisabled(roomTypeEntity.getIsDisabled());
                    roomTypeEntityToUpdate.setRoomDescription(roomTypeEntity.getRoomDescription());
                    roomTypeEntityToUpdate.setRoomImportance(roomTypeEntity.getRoomImportance());
                    roomTypeEntityToUpdate.setRoomSize(roomTypeEntity.getRoomSize());
                    roomTypeEntityToUpdate.setRoomBed(roomTypeEntity.getRoomBed());
                    roomTypeEntityToUpdate.setRoomCapacity(roomTypeEntity.getRoomCapacity());
                    roomTypeEntityToUpdate.setRoomAmenities(roomTypeEntity.getRoomAmenities());
                    entityManager.persist(roomTypeEntityToUpdate);
                }
                else
                {
                    throw new UpdateRoomTypeException("Room Type Name of roomType record to be updated does not match the existing record");
                }
            }
            else
            {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        }
        else
        {
            throw new RoomTypeNotFoundException("RoomType ID not provided for roomType to be updated");
        }
    }
    
    
    
    // need change this !! 
    
    @Override
    public void deleteRoomType(Long roomTypeId) throws RoomTypeNotFoundException, DeleteRoomTypeException
    {
        RoomTypeEntity roomTypeEntityToRemove = retrieveRoomTypeByRoomTypeId(roomTypeId);
        
        List<SaleTransactionLineItemEntity> saleTransactionLineItemEntities = saleTransactionEntitySessionBeanLocal.retrieveSaleTransactionLineItemsByRoomTypeId(roomTypeId);
        
        if(saleTransactionLineItemEntities.isEmpty())
        {
            entityManager.remove(roomTypeEntityToRemove);
        }
        else
        {
            throw new DeleteRoomTypeException("RoomType ID " + roomTypeId + " is associated with existing sale transaction line item(s) and cannot be deleted!");
        }
    }
    
}
