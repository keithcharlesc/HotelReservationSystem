package ejb.session.stateless;

import entity.GuestEntity;
import entity.PartnerEmployeeEntity;
import entity.ReservationEntity;
import entity.ReservationRoomEntity;
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
import util.exception.PartnerNotFoundException;
import util.exception.ReservationNotFoundException;
import util.exception.UnknownPersistenceException;

@Stateless

public class ReservationSessionBean implements ReservationSessionBeanLocal, ReservationSessionBeanRemote {

    @EJB
    private PartnerEmployeeSessionBeanLocal partnerEmployeeSessionBeanLocal;

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
    public List<ReservationEntity> retrieveCurrentDayReservations(Date currentDate) {  //2021-12-04 00:00:00
//        long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;
//        Query query = em.createQuery("SELECT r FROM ReservationEntity r, IN (r.reservationRooms) rr WHERE r.startDate > :currentDate AND r.startDate < :nextDay AND rr.isAllocated=false");
        Query query = em.createQuery("SELECT r FROM ReservationEntity r, IN (r.reservationRooms) rr WHERE r.startDate >= :startOfDay AND r.startDate <= :endOfDay AND rr.isAllocated=false");
//          Query query = em.createQuery("SELECT r FROM ReservationEntity r WHERE r.startDate >= :startOfDay AND r.startDate <=, IN (r.reservationRooms) rr WHERE rr.isAllocated=false");

        LocalDateTime beginning = convertToLocalDateTimeViaInstant(currentDate).truncatedTo(ChronoUnit.HOURS);
        Date beginningOfCurrentDate = convertToDateViaSqlTimestamp(beginning);
        System.out.println("beginningOfCurrentDate: " + beginningOfCurrentDate);

        LocalDateTime end = convertToLocalDateTimeViaInstant(currentDate).truncatedTo(ChronoUnit.HOURS);
        Date endOfCurrentDate = convertToDateViaSqlTimestamp(end.plusHours(23).plusMinutes(59).plusSeconds(59));
        System.out.println("endOfCurrentDate: " + endOfCurrentDate);

//        Date beginning = getDateWithoutTimeUsingFormat(currentDate);
//        currentDate 00>=   startDate <= currentDate 2359                
//query.setParameter("currentDate", currentDate, TemporalType.DATE);
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
    
    @Override
    public Long createNewReservationReturnId(Long guestId, ReservationEntity newReservationEntity, String username) throws GuestNotFoundException, InputDataValidationException, UnknownPersistenceException {
        Set<ConstraintViolation<ReservationEntity>> constraintViolations = validator.validate(newReservationEntity);
        if (constraintViolations.isEmpty() & newReservationEntity != null) {
            try {
                PartnerEmployeeEntity partnerEntity = partnerEmployeeSessionBeanLocal.retrievePartnerEmployeeByUsername(username);
                GuestEntity guestEntity = guestSessionBeanLocal.retrieveGuestByGuestId(guestId);
                newReservationEntity.setGuest(guestEntity);
                guestEntity.getReservations().add(newReservationEntity);
                partnerEntity.getReservations().add(newReservationEntity);

                em.persist(newReservationEntity);

//                for (NightEntity nightEntity : newReservationEntity.getNights()) {
//                    em.persist(nightEntity);
//                }
                for (ReservationRoomEntity reservationRoomEntity : newReservationEntity.getReservationRooms()) {
                    em.persist(reservationRoomEntity);
                }
//                em.persist(newReservationEntity.getRoomType());
                em.flush();
                Long newId = newReservationEntity.getReservationId();
                return newId;
            } catch (PersistenceException | PartnerNotFoundException ex) {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
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

//    public static Date getDateWithoutTimeUsingFormat(Date date) throws ParseException {
//        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
//        return formatter.parse(formatter.format(date));
//    }
    public LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public Date convertToDateViaSqlTimestamp(LocalDateTime dateToConvert) {
        return java.sql.Timestamp.valueOf(dateToConvert);
    }
}
