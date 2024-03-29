package ejb.session.stateless;

import entity.ReservationRoomEntity;
import entity.RoomEntity;
import entity.RoomTypeEntity;
import java.util.Date;
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
import javax.persistence.TemporalType;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.RoomStatusEnum;
import util.exception.DeleteRoomException;
import util.exception.InputDataValidationException;
import util.exception.RoomNumberExistException;
import util.exception.RoomNotFoundException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomException;


@Stateless
public class RoomSessionBean implements RoomSessionBeanRemote, RoomSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager entityManager;

    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBeanLocal;
    @EJB
    private ReservationRoomSessionBeanLocal reservationRoomSessionBeanLocal;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public RoomSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public RoomEntity createNewRoom(RoomEntity newRoomEntity, String roomTypeName) throws RoomNumberExistException, UnknownPersistenceException, InputDataValidationException, RoomTypeNotFoundException {
        Set<ConstraintViolation<RoomEntity>> constraintViolations = validator.validate(newRoomEntity);

        if (constraintViolations.isEmpty()) {
            try {

                RoomTypeEntity roomType = roomTypeSessionBeanLocal.retrieveRoomTypeByRoomTypeName(roomTypeName);
                roomType.getRooms().add(newRoomEntity);
                newRoomEntity.setRoomType(roomType);
                entityManager.persist(newRoomEntity);
                entityManager.flush();
                return newRoomEntity;
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new RoomNumberExistException();
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } catch (RoomTypeNotFoundException ex) {
                throw new RoomTypeNotFoundException("Room type " + roomTypeName + " not found!");
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    @Override
    public List<RoomEntity> retrieveAllRooms() {
        Query query = entityManager.createQuery("SELECT r FROM RoomEntity r ORDER BY r.number");
        List<RoomEntity> rooms = query.getResultList();
        for (RoomEntity room : rooms) {
            room.getRoomType();
        }

        return rooms;
    }

    @Override
    public RoomEntity retrieveRoomByRoomId(Long roomId) throws RoomNotFoundException {
        RoomEntity roomEntity = entityManager.find(RoomEntity.class, roomId);

        if (roomEntity != null) {
            roomEntity.getRoomType();
            roomEntity.getReservationRooms().size();
            return roomEntity;
        } else {
            throw new RoomNotFoundException("Room ID " + roomId + " does not exist!");
        }
    }

    @Override
    public RoomEntity retrieveRoomByRoomNumber(String number) throws RoomNotFoundException {
        Query query = entityManager.createQuery("SELECT p FROM RoomEntity p WHERE p.number = :inRoomNumber");
        query.setParameter("inRoomNumber", number);

        try {
            RoomEntity roomEntity = (RoomEntity) query.getSingleResult();
            roomEntity.getRoomType();
            roomEntity.getReservationRooms().size();
            return roomEntity;
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new RoomNotFoundException("Room Type Name " + number + " does not exist!");
        }
    }
    
    @Override 
    public List<RoomEntity> retreiveAvailableRooms(Date allocateDate) {
        Query query1 = entityManager.createQuery("SELECT r FROM RoomEntity r, IN (r.reservationRooms) rr WHERE rr.reservation.endDate <= :allocateDate AND r.roomAllocated = true");
        query1.setParameter("allocateDate", allocateDate);
        List<RoomEntity> rooms = query1.getResultList();
        for(RoomEntity room: rooms) {
            room.setRoomAllocated(false);
            entityManager.persist(room);
        }
        Query query2 = entityManager.createQuery("SELECT r FROM RoomEntity r WHERE r.roomAllocated = false AND r.isDisabled = false AND r.roomStatusEnum = :status ORDER BY r.roomType");
        query2.setParameter("status", RoomStatusEnum.AVAILABLE);
        return query2.getResultList();
    }

    @Override
    public RoomEntity updateRoom(RoomEntity roomEntity) throws RoomNotFoundException, UpdateRoomException, InputDataValidationException {
        if (roomEntity != null && roomEntity.getRoomId() != null) {
            Set<ConstraintViolation<RoomEntity>> constraintViolations = validator.validate(roomEntity);

            if (constraintViolations.isEmpty()) {
                RoomEntity roomEntityToUpdate = retrieveRoomByRoomId(roomEntity.getRoomId());

                if (roomEntityToUpdate.getNumber().equals(roomEntity.getNumber())) {
                    roomEntityToUpdate.setIsDisabled(roomEntity.getIsDisabled());
                    roomEntityToUpdate.setRoomStatusEnum(roomEntity.getRoomStatusEnum());
                    roomEntityToUpdate.setRoomAllocated(roomEntity.getRoomAllocated());
                    entityManager.persist(roomEntityToUpdate);
                    entityManager.flush();
                    return roomEntityToUpdate;
                } else {
                    throw new UpdateRoomException("Room Type Name of room record to be updated does not match the existing record");
                }
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } else {
            throw new RoomNotFoundException("Room ID not provided for room to be updated");
        }
    }


    @Override
    public void deleteRoom(Long roomId) throws RoomNotFoundException, DeleteRoomException {
        RoomEntity roomEntityToRemove = retrieveRoomByRoomId(roomId);

        List<ReservationRoomEntity> reservationRoomEntities = roomEntityToRemove.getReservationRooms();
        roomEntityToRemove.getRoomType().getRooms().remove(roomEntityToRemove);

        if (reservationRoomEntities.isEmpty()) {
            entityManager.remove(roomEntityToRemove);
        } else {
            roomEntityToRemove.setIsDisabled(true);
        }
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<RoomEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

}
