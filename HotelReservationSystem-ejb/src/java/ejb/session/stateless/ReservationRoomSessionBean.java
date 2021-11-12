package ejb.session.stateless;

import entity.ExceptionRecordEntity;
import entity.ReservationEntity;
import entity.ReservationRoomEntity;
import entity.RoomEntity;
import entity.RoomTypeEntity;
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
import util.exception.InputDataValidationException;
import util.exception.ReservationNotFoundException;
import util.exception.ReservationRoomNotFoundException;
import util.exception.RoomNotFoundException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateReservationRoomException;

@Stateless

public class ReservationRoomSessionBean implements ReservationRoomSessionBeanLocal, ReservationRoomSessionBeanRemote {

    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBean;

    @EJB
    private ExceptionRecordSessionBeanLocal exceptionRecordSessionBean;

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
    public List<ReservationRoomEntity> retrieveUnallocatedRooms(Date allocateDate) {
        Query query = em.createQuery("SELECT rr FROM ReservationRoomEntity rr WHERE rr.isAllocated = false WHERE rr.startDate >= :allocateDate ORDER BY rr.reservation.roomType");
        query.setParameter("allocateDate", allocateDate);
        return query.getResultList();
    }
    
    @Override
    public void allocateRooms(Date allocateDate) { //2021-12-04 00:00:00
        List<ReservationEntity> currentDayReservation = reservationSessionBeanLocal.retrieveCurrentDayReservations(allocateDate);
        System.out.println(currentDayReservation.get(0));
        //List<ReservationRoomEntity> unallocated = retrieveUnallocatedRooms(allocateDate);
        //List<RoomEntity> availableRooms = roomSessionBeanLocal.retreiveAvailableRooms(allocateDate);
        for (ReservationEntity reservation : currentDayReservation) {
            RoomTypeEntity roomType = reservation.getRoomType();
            int i = 0;
            List<RoomEntity> availableRooms = roomSessionBeanLocal.retreiveAvailableRooms(allocateDate, roomType);
            if(availableRooms.size() >= reservation.getNumberOfRooms()) {
                System.out.println("YES");
                for(ReservationRoomEntity reservationRoom: reservation.getReservationRooms()) {
                    System.out.println(i);
                    reservationRoom.setRoom(availableRooms.get(i));
                    availableRooms.get(i).getReservationRooms().add(reservationRoom);
                    reservationRoom.setIsAllocated(true);
                    availableRooms.get(i).setRoomAllocated(true);
                    i++;
                }
            }
        }
    }
        
        //think of how to rollback 
//        for (ReservationRoomEntity reservationRoom : unallocated) {
//            if (reservationRoom.getReservation().getRoomType().equals(availableRooms.get(i).getRoomType())) {
//                reservationRoom.setRoom(availableRooms.get(i));
//                availableRooms.get(i).getReservationRooms().add(reservationRoom);
//                reservationRoom.setIsAllocated(true);
//                availableRooms.get(i).setRoomAllocated(true);
//            }
//            i++;
//        }
    
    @Override
    public void allocateRoomExceptionType1(Date allocateDate) throws ReservationRoomNotFoundException, RoomTypeNotFoundException {
        List<ReservationEntity> currentDayReservation = reservationSessionBeanLocal.retrieveCurrentDayReservations(allocateDate);
        for (ReservationEntity reservation : currentDayReservation) {
            String next = reservation.getRoomType().getNextRoomType();
            if (!next.equals("None")) {
                RoomTypeEntity nextRoomType = roomTypeSessionBean.retrieveRoomTypeByRoomTypeName(next);
                int i = 0;
                List<RoomEntity> availableRooms = roomSessionBeanLocal.retreiveAvailableRooms(allocateDate, nextRoomType);
                if (availableRooms.size() >= reservation.getNumberOfRooms()) {
                    for (ReservationRoomEntity reservationRoom : reservation.getReservationRooms()) {
                        reservationRoom.setRoom(availableRooms.get(i));
                        availableRooms.get(i).getReservationRooms().add(reservationRoom);
                        reservationRoom.setIsAllocated(true);
                        availableRooms.get(i).setRoomAllocated(true);
                        exceptionRecordSessionBean.createNewExceptionRecord(new ExceptionRecordEntity(1), reservationRoom.getReservationRoomId());
                    }
                }
            }
        }
    }


//        List<ReservationRoomEntity> unallocated = retrieveUnallocatedRooms();
//        List<RoomEntity> availableRooms = roomSessionBeanLocal.retreiveAvailableRooms();
//        int i = 0;
//        for (ReservationRoomEntity reservationRoom : unallocated) {
//            String nextRoomType = reservationRoom.getReservation().getRoomType().getNextRoomType();
//            if (availableRooms.get(i).getRoomType().equals(nextRoomType)) {
//                reservationRoom.setRoom(availableRooms.get(i));
//                availableRooms.get(i).getReservationRooms().add(reservationRoom);
//                reservationRoom.setIsAllocated(true);
//                availableRooms.get(i).setRoomAllocated(true);
//                exceptionRecordSessionBean.createNewExceptionRecord(new ExceptionRecordEntity(1), reservationRoom.getReservationRoomId());
//            }
//            i++;
//        }
//    }
    
    @Override
    public void allocateRoomExceptionType2(Date allocateDate) throws ReservationRoomNotFoundException {
        List<ReservationEntity> currentDayReservation = reservationSessionBeanLocal.retrieveCurrentDayReservations(allocateDate);
        for (ReservationEntity reservation : currentDayReservation) {
            for (ReservationRoomEntity reservationRoom : reservation.getReservationRooms()) {
                exceptionRecordSessionBean.createNewExceptionRecord(new ExceptionRecordEntity(2), reservationRoom.getReservationRoomId());
            }
        }
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
