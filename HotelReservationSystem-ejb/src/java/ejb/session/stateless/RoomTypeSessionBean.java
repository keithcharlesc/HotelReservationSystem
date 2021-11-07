/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomEntity;
import entity.RoomRateEntity;
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

    @EJB
    private RoomRateSessionBeanLocal roomRateSessionBeanLocal;
    @EJB
    private RoomSessionBeanLocal roomSessionBeanLocal;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public RoomTypeSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public RoomTypeEntity createNewRoomType(RoomTypeEntity newRoomTypeEntity) throws RoomTypeNameExistException, UnknownPersistenceException, InputDataValidationException, UpdateRoomTypeException {
        Set<ConstraintViolation<RoomTypeEntity>> constraintViolations = validator.validate(newRoomTypeEntity);

        if (constraintViolations.isEmpty()) {
            try {
                entityManager.persist(newRoomTypeEntity);

                Query query = entityManager.createQuery("SELECT r FROM RoomTypeEntity r WHERE r.nextRoomType = :nextRoomType");
                query.setParameter("nextRoomType", newRoomTypeEntity.getNextRoomType());
                if(query.getSingleResult() != null) {
                    RoomTypeEntity roomType = (RoomTypeEntity) query.getSingleResult();
                    roomType.setNextRoomType(newRoomTypeEntity.getRoomTypeName());
                } else {
                    throw new UpdateRoomTypeException("Unable to change nextRoomType of previous room!");
                }

                if (newRoomTypeEntity.getRooms().size() > 0) {
                    for (RoomEntity room : newRoomTypeEntity.getRooms()) {
                        entityManager.persist(room);
                    }
                }

                if (newRoomTypeEntity.getRoomRates().size() > 0) {
                    for (RoomRateEntity roomRate : newRoomTypeEntity.getRoomRates()) {
                        entityManager.persist(roomRate);
                    }
                }

                entityManager.flush();

                return newRoomTypeEntity;
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new RoomTypeNameExistException();
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
    public List<RoomTypeEntity> retrieveAllRoomTypes() {
        Query query = entityManager.createQuery("SELECT r FROM RoomTypeEntity r ORDER BY r.roomTypeName");

        return query.getResultList();
    }

    @Override
    public RoomTypeEntity retrieveRoomTypeByRoomTypeId(Long roomTypeId) throws RoomTypeNotFoundException {
        RoomTypeEntity roomTypeEntity = entityManager.find(RoomTypeEntity.class, roomTypeId);

        if (roomTypeEntity != null) {
            roomTypeEntity.getRooms().size();
            roomTypeEntity.getRoomRates().size();
            return roomTypeEntity;
        } else {
            throw new RoomTypeNotFoundException("RoomType ID " + roomTypeId + " does not exist!");
        }
    }

    @Override
    public RoomTypeEntity retrieveRoomTypeByRoomTypeName(String name) throws RoomTypeNotFoundException {
        Query query = entityManager.createQuery("SELECT p FROM RoomTypeEntity p WHERE p.roomTypeName = :inRoomTypeName");
        query.setParameter("inRoomTypeName", name);

        try {
            RoomTypeEntity roomTypeEntity = (RoomTypeEntity) query.getSingleResult();
            roomTypeEntity.getRooms().size();
            roomTypeEntity.getRoomRates().size();
            return roomTypeEntity;
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new RoomTypeNotFoundException("Room Type Name " + name + " does not exist!");
        }
    }

    @Override
    public void updateRoomType(RoomTypeEntity roomTypeEntity) throws RoomTypeNotFoundException, UpdateRoomTypeException, InputDataValidationException {
        if (roomTypeEntity != null && roomTypeEntity.getRoomTypeId() != null) {
            Set<ConstraintViolation<RoomTypeEntity>> constraintViolations = validator.validate(roomTypeEntity);

            if (constraintViolations.isEmpty()) {
                RoomTypeEntity roomTypeEntityToUpdate = retrieveRoomTypeByRoomTypeId(roomTypeEntity.getRoomTypeId());

                if (roomTypeEntityToUpdate.getRoomTypeName().equals(roomTypeEntity.getRoomTypeName())) {
                    roomTypeEntityToUpdate.setIsDisabled(roomTypeEntity.getIsDisabled());
                    roomTypeEntityToUpdate.setRoomDescription(roomTypeEntity.getRoomDescription());
                    roomTypeEntityToUpdate.setRoomSize(roomTypeEntity.getRoomSize());
                    roomTypeEntityToUpdate.setRoomBed(roomTypeEntity.getRoomBed());
                    roomTypeEntityToUpdate.setRoomCapacity(roomTypeEntity.getRoomCapacity());
                    roomTypeEntityToUpdate.setRoomAmenities(roomTypeEntity.getRoomAmenities());
                    roomTypeEntityToUpdate.setNextRoomType(roomTypeEntity.getNextRoomType());
                    entityManager.persist(roomTypeEntityToUpdate);
                } else {
                    throw new UpdateRoomTypeException("Room Type Name of roomType record to be updated does not match the existing record");
                }
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } else {
            throw new RoomTypeNotFoundException("RoomType ID not provided for roomType to be updated");
        }
    }

    @Override
    public void deleteRoomType(Long roomTypeId) throws RoomTypeNotFoundException, DeleteRoomTypeException, UpdateRoomTypeException {
        RoomTypeEntity roomTypeEntityToRemove = retrieveRoomTypeByRoomTypeId(roomTypeId);

        List<RoomRateEntity> roomRateEntities = roomTypeEntityToRemove.getRoomRates();
        List<RoomEntity> rooms = roomTypeEntityToRemove.getRooms();
        
        Query query = entityManager.createQuery("SELECT r FROM RoomTypeEntity r WHERE r.nextRoomType = :nextRoomType");
            query.setParameter("nextRoomType", roomTypeEntityToRemove.getRoomTypeName());
            if (query.getSingleResult() != null) {
                RoomTypeEntity roomType = (RoomTypeEntity) query.getSingleResult();
                roomType.setNextRoomType(roomTypeEntityToRemove.getNextRoomType());
            } else {
                throw new UpdateRoomTypeException("Unable to change nextRoomType of previous room!");
            }

        if (roomRateEntities.isEmpty() && rooms.isEmpty()) {
            entityManager.remove(roomTypeEntityToRemove);
        } else {
            for (RoomRateEntity roomRate : roomRateEntities) {
                roomRate.setIsDisabled(true);
            }
            for (RoomEntity room : rooms) {
                room.setIsDisabled(true);
            }
            roomTypeEntityToRemove.setIsDisabled(true);
        }
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<RoomTypeEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

}
