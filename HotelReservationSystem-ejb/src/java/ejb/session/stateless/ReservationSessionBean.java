package ejb.session.stateless;

import entity.GuestEntity;
import entity.ReservationEntity;
import entity.ReservationRoomEntity;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.GuestNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.ReservationNotFoundException;
import util.exception.UnknownPersistenceException;

@Stateless

public class ReservationSessionBean implements ReservationSessionBeanLocal, ReservationSessionBeanRemote {

    @EJB
    private RoomRateSessionBeanLocal roomRateSessionBeanLocal;

    @EJB
    private GuestSessionBeanLocal guestSessionBeanLocal;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    // Added in v4.2 for bean validation
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public ReservationSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    // Updated in v4.1
    // Updated in v4.2 with bean validation
    @Override
    public ReservationEntity createNewReservation(Long guestId, ReservationEntity newReservationEntity) throws GuestNotFoundException, InputDataValidationException, UnknownPersistenceException {

        Set<ConstraintViolation<ReservationEntity>> constraintViolations = validator.validate(newReservationEntity);
        if (constraintViolations.isEmpty() & newReservationEntity != null) {
            try {
                GuestEntity guestEntity = guestSessionBeanLocal.retrieveGuestByGuestId(guestId);
                newReservationEntity.setGuest(guestEntity);
                guestEntity.getReservations().add(newReservationEntity);

                em.persist(newReservationEntity);

//                for (NightEntity nightEntity : newReservationEntity.getNights()) {
//                    em.persist(nightEntity);
//                }

                for (ReservationRoomEntity reservationRoomEntity : newReservationEntity.getReservationRooms()) {
                    em.persist(reservationRoomEntity);
                }
//                em.persist(newReservationEntity.getRoomType());
                em.flush();

                return newReservationEntity;
            } catch (PersistenceException ex) {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    @Override
    public List<ReservationEntity> retrieveAllReservations() {
        Query query = em.createQuery("SELECT r FROM ReservationEntity r");

        return query.getResultList();
    }
    
    @Override
    public List<ReservationEntity> retrieveCurrentDayReservations(Date currentDate) {
        Query query = em.createQuery("SELECT r FROM ReservationEntity r, IN (r.reservationRooms) rr WHERE r.startDate = :currentDate AND rr.isAllocated=FALSE");
        query.setParameter("currentDate", currentDate, TemporalType.DATE);
        List<ReservationEntity> reservations = query.getResultList();
        for(ReservationEntity reservation: reservations) {
            reservation.getReservationRooms().size();
        }

        return reservations;
    }

    @Override
    public ReservationEntity retrieveReservationByReservationId(Long reservationId) throws ReservationNotFoundException {
        ReservationEntity reservationEntity = em.find(ReservationEntity.class, reservationId);

        if (reservationEntity != null) {
            reservationEntity.getGuest();
            reservationEntity.getReservationRooms().size();
            reservationEntity.getRoomType();
            reservationEntity.getNights().size();
            return reservationEntity;
        } else {
            throw new ReservationNotFoundException("Reservation ID " + reservationId + " does not exist!");
        }
    }

//    @Override
//    public void updateReservation(ReservationEntity reservationEntity) throws ReservationNotFoundException, UpdateReservationException, InputDataValidationException {
//        if (reservationEntity != null && reservationEntity.getReservationId() != null) {
//            Set<ConstraintViolation<ReservationEntity>> constraintViolations = validator.validate(reservationEntity);
//
//            if (constraintViolations.isEmpty()) {
//                ReservationEntity reservationEntityToUpdate = retrieveReservationByReservationId(reservationEntity.getReservationId());
//
//                if (reservationEntityToUpdate.getEmail().equals(reservationEntity.getEmail())) {
//                    reservationEntityToUpdate.setName(reservationEntity.getName());
//                    reservationEntityToUpdate.setPhoneNumber(reservationEntity.getLastName());
//                    // Username and password are deliberately NOT updated to demonstrate that client is not allowed to update account credential through this business method
//                } else {
//                    throw new UpdateReservationException("Username of reservation record to be updated does not match the existing record");
//                }
//            } else {
//                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
//            }
//        } else {
//            throw new ReservationNotFoundException("Reservation ID not provided for reservation to be updated");
//        }
//    }
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<ReservationEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

}
