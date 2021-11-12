/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomEntity;
import entity.RoomRateEntity;
import entity.RoomTypeEntity;
import java.util.ArrayList;
import java.util.Calendar;
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
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.RoomStatusEnum;
import util.exception.DeleteRoomTypeException;
import util.exception.InputDataValidationException;
import util.exception.RoomTypeNameExistException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomTypeException;

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
                entityManager.persist(newRoomTypeEntity);
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
    public RoomTypeEntity insertNewRoomType(RoomTypeEntity newRoomTypeEntity) throws RoomTypeNameExistException, UnknownPersistenceException, InputDataValidationException, UpdateRoomTypeException {
        Set<ConstraintViolation<RoomTypeEntity>> constraintViolations = validator.validate(newRoomTypeEntity);

        if (constraintViolations.isEmpty()) {
            try {
                //checks whether a room type has next room type the same as that next room type
                Query query = entityManager.createQuery("SELECT r FROM RoomTypeEntity r WHERE r.nextRoomType = :nextRoomType");
                query.setParameter("nextRoomType", newRoomTypeEntity.getNextRoomType());

//                //checks whether the next room type exists as a Room Type itself
//                Query queryTwo = entityManager.createQuery("SELECT r FROM RoomTypeEntity r WHERE r.roomTypeName = :roomType");
//                queryTwo.setParameter("roomType", newRoomTypeEntity.getNextRoomType());
//                RoomTypeEntity roomTypeExists = (RoomTypeEntity) queryTwo.getSingleResult();
                if (!query.getResultList().isEmpty()) {
                    RoomTypeEntity roomType = (RoomTypeEntity) query.getSingleResult();
//                    System.out.println("Executed One");
                    roomType.setNextRoomType(newRoomTypeEntity.getRoomTypeName());
                    createNewRoomType(newRoomTypeEntity);
                    entityManager.persist(newRoomTypeEntity);
                    entityManager.flush();
                    return newRoomTypeEntity;
                } else {
//                    System.out.println("Executed Two");
                    createNewRoomType(newRoomTypeEntity);
                    entityManager.persist(newRoomTypeEntity);
                    entityManager.flush();
                    return newRoomTypeEntity;
                }
            } catch (NoResultException ex) {
                throw new UnknownPersistenceException("FAILED");
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
        Query query = entityManager.createQuery("SELECT r FROM RoomTypeEntity r ORDER BY r.roomTypeId");

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
                String initialRoomName = roomTypeEntityToUpdate.getRoomTypeName();
                String initialNextRoomType = roomTypeEntity.getNextRoomType();
                
                //update next room type - the one before original place 
                if (!initialNextRoomType.equals(roomTypeEntity.getNextRoomType())) {
                    Query query1 = entityManager.createQuery("SELECT r FROM RoomTypeEntity r WHERE r.nextRoomType = :roomType");
                    query1.setParameter("roomType", initialRoomName);
                    if (!query1.getResultList().isEmpty()) {
                        RoomTypeEntity roomType = (RoomTypeEntity) query1.getSingleResult();
                        roomType.setNextRoomType(roomTypeEntityToUpdate.getNextRoomType());
                    }

                    Query query2 = entityManager.createQuery("SELECT r FROM RoomTypeEntity r WHERE r.nextRoomType = :nextRoomType");
                    query2.setParameter("nextRoomType", roomTypeEntity.getNextRoomType());
                    if (!query2.getResultList().isEmpty()) {
                        RoomTypeEntity roomType = (RoomTypeEntity) query2.getSingleResult();
                        System.out.println(roomType.getRoomTypeName());
                        roomType.setNextRoomType(roomTypeEntity.getRoomTypeName());
                    }
                    roomTypeEntityToUpdate.setNextRoomType(roomTypeEntity.getNextRoomType());
                }
//                
                Query query = entityManager.createQuery("SELECT r FROM RoomTypeEntity r WHERE r.nextRoomType = :nextRoomType");
                query.setParameter("nextRoomType", initialRoomName);
                if (!query.getResultList().isEmpty()) {
                    RoomTypeEntity roomBefore = (RoomTypeEntity) query.getSingleResult();
                    roomBefore.setNextRoomType(roomTypeEntity.getRoomTypeName());
                }
                
                roomTypeEntityToUpdate.setRoomTypeName(roomTypeEntity.getRoomTypeName());

                //if (roomTypeEntityToUpdate.getRoomTypeName().equals(roomTypeEntity.getRoomTypeName())) {
                //roomTypeEntityToUpdate.setIsDisabled(roomTypeEntity.getIsDisabled());
                roomTypeEntityToUpdate.setRoomDescription(roomTypeEntity.getRoomDescription());
                roomTypeEntityToUpdate.setRoomSize(roomTypeEntity.getRoomSize());
                roomTypeEntityToUpdate.setRoomBed(roomTypeEntity.getRoomBed());
                roomTypeEntityToUpdate.setRoomCapacity(roomTypeEntity.getRoomCapacity());
                roomTypeEntityToUpdate.setRoomAmenities(roomTypeEntity.getRoomAmenities());
                
                entityManager.persist(roomTypeEntityToUpdate);

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
        if (!query.getResultList().isEmpty()) {
            RoomTypeEntity roomType = (RoomTypeEntity) query.getSingleResult();
            roomType.setNextRoomType(roomTypeEntityToRemove.getNextRoomType());
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

//    @Override
//    public List<RoomTypeEntity> retrieveAllRoomTypesThatHasAvailableRooms() {
////        Query query = entityManager.createQuery("SELECT r FROM RoomTypeEntity r ORDER BY r.roomTypeId");
//        Query query = entityManager.createQuery("SELECT COUNT(r) FROM Room r, IN (r.roomType) rt");
//        return query.getResultList();
//        
//        RoomTypeEntity newRoomTypeEntity
//        
//         Query query = entityManager.createQuery("SELECT r FROM RoomTypeEntity r WHERE r.nextRoomType = :nextRoomType");
//                query.setParameter("nextRoomType", newRoomTypeEntity.getNextRoomType());
//        
//    }
    
    @Override
    public Integer retrieveTotalQuantityOfRoomsBasedOnRoomType(String roomTypeInput) { //RETURNS TOTAL QUANTITY OF ROOMS - ROOM TYPE
        Query query = entityManager.createQuery("SELECT r FROM RoomEntity r JOIN r.roomType rt WHERE rt.roomTypeName = :roomType AND r.roomStatusEnum = :roomStatus");
        query.setParameter("roomStatus", RoomStatusEnum.AVAILABLE);
        query.setParameter("roomType", roomTypeInput);
        return query.getResultList().size();
    }

    //Checks against check-in date and check-out date of the new reservation 
    //For those reservations made previously, how many rooms have been reserved alr 
    @Override
    public Integer retrieveQuantityOfRoomsReserved(Date checkInDateInput, Date checkOutDateInput, String roomTypeInput) {
        Query query = entityManager.createQuery("SELECT rre FROM ReservationRoomEntity rre WHERE rre.reservation.roomType.roomTypeName = :roomType AND rre.reservation.startDate >= :checkInDate AND rre.reservation.startDate < :checkOutDate");
        query.setParameter("checkInDate", checkInDateInput);
        query.setParameter("checkOutDate", checkOutDateInput);
        query.setParameter("roomType", roomTypeInput);
        return query.getResultList().size();
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<RoomTypeEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
    
    public List<Date> getListOfDaysBetweenTwoDates(Date startDate, Date endDate) {
        List<Date> result = new ArrayList<Date>();
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);
//        end.add(Calendar.DAY_OF_YEAR, 1); //Add 1 day to endDate to make sure endDate is included into the final list
        while (start.before(end)) {
            result.add(start.getTime());
            start.add(Calendar.DAY_OF_YEAR, 1);
        }
        return result;
    }

}
