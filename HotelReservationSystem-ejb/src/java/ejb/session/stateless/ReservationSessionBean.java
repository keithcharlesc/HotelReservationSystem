package ejb.session.stateless;

import entity.GuestEntity;
import entity.ReservationEntity;
import entity.ReservationRoomEntity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
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

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public ReservationSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }


    @Override
    public ReservationEntity createNewReservation(Long guestId, ReservationEntity newReservationEntity) throws GuestNotFoundException, InputDataValidationException, UnknownPersistenceException {

        Set<ConstraintViolation<ReservationEntity>> constraintViolations = validator.validate(newReservationEntity);
        if (constraintViolations.isEmpty() & newReservationEntity != null) {
            try {
                GuestEntity guestEntity = guestSessionBeanLocal.retrieveGuestByGuestId(guestId);
                newReservationEntity.setGuest(guestEntity);
                guestEntity.getReservations().add(newReservationEntity);

                em.persist(newReservationEntity);
                for (ReservationRoomEntity reservationRoomEntity : newReservationEntity.getReservationRooms()) {
                    em.persist(reservationRoomEntity);
                }
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
        Query query = em.createQuery("SELECT r FROM ReservationEntity r, IN (r.reservationRooms) rr WHERE r.startDate >= :startOfDay AND r.startDate <= :endOfDay AND rr.isAllocated=false");

        LocalDateTime beginning = convertToLocalDateTimeViaInstant(currentDate).truncatedTo(ChronoUnit.HOURS);
        Date beginningOfCurrentDate = convertToDateViaSqlTimestamp(beginning);
        System.out.println("beginningOfCurrentDate: " + beginningOfCurrentDate);

        LocalDateTime end = convertToLocalDateTimeViaInstant(currentDate).truncatedTo(ChronoUnit.HOURS);
        Date endOfCurrentDate = convertToDateViaSqlTimestamp(end.plusHours(23).plusMinutes(59).plusSeconds(59));
        System.out.println("endOfCurrentDate: " + endOfCurrentDate);

        query.setParameter("startOfDay", beginningOfCurrentDate);
        query.setParameter("endOfDay", endOfCurrentDate);
        List<ReservationEntity> reservations = query.getResultList();
        System.out.println("reservation.getReservationRooms().size(): " + reservations.size());
        for (ReservationEntity reservation : reservations) {
            System.out.println("reservation: " + reservation.getReservationId());
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

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<ReservationEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

    public LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public Date convertToDateViaSqlTimestamp(LocalDateTime dateToConvert) {
        return java.sql.Timestamp.valueOf(dateToConvert);
    }
}
