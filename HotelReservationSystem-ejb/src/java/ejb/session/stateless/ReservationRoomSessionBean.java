package ejb.session.stateless;

import entity.ReservationEntity;
import entity.ReservationRoomEntity;
import entity.RoomEntity;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.InputDataValidationException;
import util.exception.ReservationNotFoundException;
import util.exception.ReservationRoomNotFoundException;
import util.exception.RoomNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateReservationRoomException;

@Stateless

public class ReservationRoomSessionBean implements ReservationRoomSessionBeanLocal, ReservationRoomSessionBeanRemote {

    @EJB
    private RoomSessionBeanLocal roomSessionBeanLocal;

    @EJB
    private ReservationSessionBeanLocal reservationSessionBeanLocal;

    

    // Added in v4.2 for bean validation
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public ReservationRoomSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public ReservationRoomEntity createNewReservationRoom(Long reservationId, ReservationRoomEntity newReservationRoomEntity) throws ReservationNotFoundException, UnknownPersistenceException, InputDataValidationException {

        Set<ConstraintViolation<ReservationRoomEntity>> constraintViolations = validator.validate(newReservationRoomEntity);
        if (constraintViolations.isEmpty() & newReservationRoomEntity != null) {
            try {
                ReservationEntity reservationEntity = reservationSessionBeanLocal.retrieveReservationByReservationId(reservationId);
                newReservationRoomEntity.setReservation(reservationEntity);
                reservationEntity.getReservationRooms().add(newReservationRoomEntity);

                em.persist(newReservationRoomEntity);
                em.flush();

                return newReservationRoomEntity;
            } catch (PersistenceException ex) {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    @Override
    public List<ReservationRoomEntity> retrieveAllReservationRooms() {
        Query query = em.createQuery("SELECT rr FROM ReservationRoomEntity rr");

        return query.getResultList();
    }

    @Override
    public ReservationRoomEntity retrieveReservationRoomByReservationRoomId(Long reservationRoomId) throws ReservationRoomNotFoundException {
        ReservationRoomEntity reservationRoomEntity = em.find(ReservationRoomEntity.class, reservationRoomId);

        if (reservationRoomEntity != null) {
            reservationRoomEntity.getReservation();
            reservationRoomEntity.getExceptionRecord();
            reservationRoomEntity.getRoom();
            return reservationRoomEntity;
        } else {
            throw new ReservationRoomNotFoundException("ReservationRoom ID " + reservationRoomId + " does not exist!");
        }
    }

    @Override
    public void updateReservationRoom(ReservationRoomEntity reservationRoomEntity) throws ReservationRoomNotFoundException, UpdateReservationRoomException, InputDataValidationException {
        if (reservationRoomEntity != null && reservationRoomEntity.getReservationRoomId() != null) {
            Set<ConstraintViolation<ReservationRoomEntity>> constraintViolations = validator.validate(reservationRoomEntity);

            if (constraintViolations.isEmpty()) {
                ReservationRoomEntity reservationRoomEntityToUpdate = retrieveReservationRoomByReservationRoomId(reservationRoomEntity.getReservationRoomId());
                reservationRoomEntityToUpdate.setIsAllocated(reservationRoomEntity.getIsAllocated());
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } else {
            throw new ReservationRoomNotFoundException("ReservationRoom ID not provided for reservationRoom to be updated");
        }
    }

    @Override
    public void associateReservationRoomWithARoom(Long reservationRoomId, Long roomId) {

        try {
            ReservationRoomEntity reservationRoom = retrieveReservationRoomByReservationRoomId(reservationRoomId);
            RoomEntity room = roomSessionBeanLocal.retrieveRoomByRoomId(roomId);
            if (reservationRoom.getRoom() != null) {
                reservationRoom.getRoom().getReservationRooms().remove(reservationRoom);
            }
            reservationRoom.setRoom(room);
            room.getReservationRooms().add(reservationRoom);

        } catch (ReservationRoomNotFoundException ex) {
            System.out.println("Reservation room not found!");
        } catch (RoomNotFoundException ex) {
            System.out.println("Room not found!");
        }

    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<ReservationRoomEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

    public void persist(Object object) {
        em.persist(object);
    }
}