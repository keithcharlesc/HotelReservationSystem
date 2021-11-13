package ejb.session.stateless;

import entity.CustomerEntity;
import entity.GuestEntity;
import entity.ReservationEntity;
import entity.ReservationRoomEntity;
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
import util.exception.DeleteGuestException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.GuestNotFoundException;
import util.exception.GuestEmailExistException;
import util.exception.UnknownPersistenceException;

@Stateless

public class GuestSessionBean implements GuestSessionBeanLocal, GuestSessionBeanRemote {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    // Added in v4.2 for bean validation
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public GuestSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    
    @Override
    public Long createNewGuest(GuestEntity newGuestEntity) throws GuestEmailExistException, UnknownPersistenceException, InputDataValidationException {
        Set<ConstraintViolation<GuestEntity>> constraintViolations = validator.validate(newGuestEntity);

        if (constraintViolations.isEmpty()) {
            try {
                em.persist(newGuestEntity);
                em.flush();

                return newGuestEntity.getGuestId();
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new GuestEmailExistException();
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
    public List<GuestEntity> retrieveAllGuests() {
        Query query = em.createQuery("SELECT s FROM GuestEntity s");

        return query.getResultList();
    }

    @Override
    public GuestEntity retrieveGuestByGuestId(Long guestId) throws GuestNotFoundException {
        GuestEntity guestEntity = em.find(GuestEntity.class, guestId);

        if (guestEntity != null) {
            guestEntity.getReservations().size();
            for(ReservationEntity reservation: guestEntity.getReservations()) {
                reservation.getRoomType();
            }
            return guestEntity;
        } else {
            throw new GuestNotFoundException("Guest ID " + guestId + " does not exist!");
        }
    }
    
    @Override
    public GuestEntity retreiveGuestReservations(String guestEmail) throws GuestNotFoundException {
        GuestEntity guest = this.retrieveGuestByEmail(guestEmail);
        guest.getReservations().size();
        for (ReservationEntity reservation : guest.getReservations()) {
            reservation.getReservationRooms().size();
            reservation.getRoomType();
            for (ReservationRoomEntity reservationRoom : reservation.getReservationRooms()) {
                reservationRoom.getExceptionRecord();
                reservationRoom.getRoom();
            }
        }
        return guest;
    }

    @Override
    public GuestEntity retrieveGuestByEmail(String email) throws GuestNotFoundException {
        Query query = em.createQuery("SELECT s FROM GuestEntity s WHERE s.email = :inEmail");
        query.setParameter("inEmail", email);

        try {
            GuestEntity guest = (GuestEntity) query.getSingleResult();
            return guest;
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new GuestNotFoundException("Guest Email " + email + " does not exist!");
        }
    }

    @Override
    public CustomerEntity guestLogin(String email, String password) throws InvalidLoginCredentialException {
        try {
            GuestEntity guestEntity = retrieveGuestByEmail(email);
            CustomerEntity customerEntity = (CustomerEntity) guestEntity;

            if (customerEntity.getPassword().equals(password)) {
                return customerEntity;
            } else {
                throw new InvalidLoginCredentialException("Email does not exist or invalid password!");
            }
        } catch (GuestNotFoundException ex) {
            throw new InvalidLoginCredentialException("Email does not exist or invalid password!");
        }
    }
    

    // Updated in v4.1 to update selective attributes instead of merging the entire state passed in from the client
    // Also check for existing guest before proceeding with the update
    // Updated in v4.2 with bean validation
//    @Override
//    public void updateGuest(GuestEntity guestEntity) throws GuestNotFoundException, UpdateGuestException, InputDataValidationException {
//        if (guestEntity != null && guestEntity.getGuestId() != null) {
//            Set<ConstraintViolation<GuestEntity>> constraintViolations = validator.validate(guestEntity);
//
//            if (constraintViolations.isEmpty()) {
//                GuestEntity guestEntityToUpdate = retrieveGuestByGuestId(guestEntity.getGuestId());
//
//                if (guestEntityToUpdate.getEmail().equals(guestEntity.getEmail())) {
//                    guestEntityToUpdate.setName(guestEntity.getName());
//                    guestEntityToUpdate.setPhoneNumber(guestEntity.getLastName());
//                    // Username and password are deliberately NOT updated to demonstrate that client is not allowed to update account credential through this business method
//                } else {
//                    throw new UpdateGuestException("Username of guest record to be updated does not match the existing record");
//                }
//            } else {
//                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
//            }
//        } else {
//            throw new GuestNotFoundException("Guest ID not provided for guest to be updated");
//        }
//    }
    // Updated in v4.1
    @Override
    public void deleteGuest(Long guestId) throws GuestNotFoundException, DeleteGuestException {
        GuestEntity guestEntityToRemove = retrieveGuestByGuestId(guestId);

//        if (guestEntityToRemove.getSaleTransactionEntities().isEmpty()) {
        em.remove(guestEntityToRemove);
//        } else {
        // New in v4.1 to prevent deleting guest with existing sale transaction(s)
//            throw new DeleteGuestException("Guest ID " + guestId + " is associated with existing sale transaction(s) and cannot be deleted!");
//        }
    }

//    @Override
//    public List<ReservationEntity> retrieveGuestReservations(Long guestId) throws RetrieveGuestReservationsException {
//
//        try {
//            GuestEntity guestEntity = retrieveGuestByGuestId(guestId);
//            guestEntity.getReservations().size();
//            if (!guestEntity.getReservations().isEmpty()) {
//                return guestEntity.getReservations();
//            } else {
//                throw new RetrieveGuestReservationsException("No reservations found!");
//            }
//
//        } catch (GuestNotFoundException ex) {
//            throw new RetrieveGuestReservationsException("Guest data not found!");
//        }
//    }

//    @Override
//    public ReservationEntity retrieveReservation(Long guestId, Long reservationId) {
//        
//    }

    // Added in v4.2
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<GuestEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
