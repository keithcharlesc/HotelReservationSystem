/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.ExceptionRecordEntity;
import entity.ReservationRoomEntity;
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
import util.exception.ExceptionRecordNotFoundException;
import util.exception.ReservationRoomNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author xianhui
 */
@Stateless
public class ExceptionRecordSessionBean implements ExceptionRecordSessionBeanRemote, ExceptionRecordSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager entityManager;
    
    @EJB
    private ReservationRoomSessionBeanLocal reservationRoomSessionBeanLocal;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public ExceptionRecordSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public ExceptionRecordEntity createNewExceptionRecord(ExceptionRecordEntity newExceptionRecordEntity, Long reservationRoomId) throws UnknownPersistenceException, InputDataValidationException, ReservationRoomNotFoundException {
        Set<ConstraintViolation<ExceptionRecordEntity>> constraintViolations = validator.validate(newExceptionRecordEntity);

        if (constraintViolations.isEmpty()) {
            try {
                entityManager.persist(newExceptionRecordEntity);

                ReservationRoomEntity reservationRoom = reservationRoomSessionBeanLocal.retrieveReservationRoomByReservationRoomId(reservationRoomId);
                reservationRoom.setExceptionRecord(newExceptionRecordEntity);
                newExceptionRecordEntity.setReservationRoom(reservationRoom);
                entityManager.flush();

                return newExceptionRecordEntity;
            } catch (PersistenceException ex) {
                throw new UnknownPersistenceException(ex.getMessage());
            } catch (ReservationRoomNotFoundException ex) {
                throw new ReservationRoomNotFoundException("Reservation room not found!");
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    @Override
    public List<ExceptionRecordEntity> retrieveAllExceptionRecords() {
        Query query = entityManager.createQuery("SELECT e FROM ExceptionRecordEntity e");

        return query.getResultList();
    }
    
    //check JPQL if falsed can be equate this way 
    @Override
    public List<ExceptionRecordEntity> retrieveUnresolvedExceptionRecords() {
        Query query = entityManager.createQuery("SELECT e FROM ExceptionRecordEntity e WHERE e.resolved = false ORDER BY e.typeOfException");

        return query.getResultList();
    }

    @Override
    public ExceptionRecordEntity retrieveExceptionRecordByExceptionRecordId(Long exceptionRecordId) throws ExceptionRecordNotFoundException {
        ExceptionRecordEntity exceptionRecordEntity = entityManager.find(ExceptionRecordEntity.class, exceptionRecordId);

        if (exceptionRecordEntity != null) {
            exceptionRecordEntity.getReservationRoom();
            return exceptionRecordEntity;
        } else {
            throw new ExceptionRecordNotFoundException("ExceptionRecord ID " + exceptionRecordId + " does not exist!");
        }
    }

    @Override
    public void updateExceptionRecord(ExceptionRecordEntity exceptionRecordEntity) throws ExceptionRecordNotFoundException, InputDataValidationException {
        if (exceptionRecordEntity != null && exceptionRecordEntity.getExceptionRecordId() != null) {
            Set<ConstraintViolation<ExceptionRecordEntity>> constraintViolations = validator.validate(exceptionRecordEntity);

            if (constraintViolations.isEmpty()) {
                ExceptionRecordEntity exceptionRecordEntityToUpdate = retrieveExceptionRecordByExceptionRecordId(exceptionRecordEntity.getExceptionRecordId());
                exceptionRecordEntityToUpdate.setResolved(exceptionRecordEntity.getResolved());
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } else {
            throw new ExceptionRecordNotFoundException("ExceptionRecord ID not provided for exceptionRecord to be updated");
        }
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<ExceptionRecordEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
