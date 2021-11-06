/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.NightEntity;
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
import util.exception.NightNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author xianhui
 */
@Stateless
public class NightSessionBean implements NightSessionBeanRemote, NightSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager entityManager;
    
    @EJB
    private RoomRateSessionBeanLocal roomRateSessionBeanLocal;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public NightSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public NightEntity createNewNight(NightEntity newNightEntity, String reservationRoomId) throws UnknownPersistenceException, InputDataValidationException {
        Set<ConstraintViolation<NightEntity>> constraintViolations = validator.validate(newNightEntity);

        if (constraintViolations.isEmpty()) {
            try {
                entityManager.persist(newNightEntity);

                ReservationRoomEntity reservationRoom = ReservationRoomSessionBeanLocal.retrieveReservationRoomByReservationRoomId(reservationRoomId);
                reservationRoom.setNight(newNightEntity);
                newNightEntity.setReservationRoom(reservationRoom);
                entityManager.flush();

                return newNightEntity;
            } catch (PersistenceException ex) {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    @Override
    public List<NightEntity> retrieveAllNights() {
        Query query = entityManager.createQuery("SELECT e FROM NightEntity e");

        return query.getResultList();
    }
    
    //check JPQL if falsed can be equate this way 
    @Override
    public List<NightEntity> retrieveUnresolvedNights() {
        Query query = entityManager.createQuery("SELECT e FROM NightEntity e WHERE e.resolved = false ORDER BY e.typeOfException");

        return query.getResultList();
    }

    @Override
    public NightEntity retrieveNightByNightId(Long nightId) throws NightNotFoundException {
        NightEntity nightEntity = entityManager.find(NightEntity.class, nightId);

        if (nightEntity != null) {
            nightEntity.getReservationRoom();
            return nightEntity;
        } else {
            throw new NightNotFoundException("Night ID " + nightId + " does not exist!");
        }
    }

    @Override
    public void updateNight(NightEntity nightEntity) throws NightNotFoundException, InputDataValidationException {
        if (nightEntity != null && nightEntity.getNightId() != null) {
            Set<ConstraintViolation<NightEntity>> constraintViolations = validator.validate(nightEntity);

            if (constraintViolations.isEmpty()) {
                NightEntity nightEntityToUpdate = retrieveNightByNightId(nightEntity.getNightId());
                nightEntityToUpdate.setResolved(nightEntity.getResolved());
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } else {
            throw new NightNotFoundException("Night ID not provided for night to be updated");
        }
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<NightEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
}
