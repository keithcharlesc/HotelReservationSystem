/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.NightEntity;
import entity.RoomRateEntity;
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
import util.exception.RoomRateNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author xianhui
 */
@Stateless
public class NightSessionBean implements NightSessionBeanRemote, NightSessionBeanLocal {

    @EJB
    private RoomRateSessionBeanLocal roomRateSessionBeanLocal;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager entityManager;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public NightSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public NightEntity createNewNight(NightEntity newNightEntity, String roomRateName) throws UnknownPersistenceException, InputDataValidationException, RoomRateNotFoundException {
        Set<ConstraintViolation<NightEntity>> constraintViolations = validator.validate(newNightEntity);

        if (constraintViolations.isEmpty()) {
            try {

                RoomRateEntity roomRate = roomRateSessionBeanLocal.retrieveRoomRateByRoomRateName(roomRateName);
                newNightEntity.setRoomRate(roomRate);
                roomRate.getNights().add(newNightEntity);

                entityManager.persist(newNightEntity);
                entityManager.flush();

                return newNightEntity;
            } catch (PersistenceException ex) {
                throw new UnknownPersistenceException(ex.getMessage());
            } catch (RoomRateNotFoundException ex) {
                throw new RoomRateNotFoundException(ex.getMessage());
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    @Override
    public List<NightEntity> retrieveAllNights() {
        Query query = entityManager.createQuery("SELECT n FROM NightEntity n");

        return query.getResultList();
    }

    @Override
    public NightEntity retrieveNightByNightId(Long nightId) throws NightNotFoundException {
        NightEntity nightEntity = entityManager.find(NightEntity.class, nightId);

        if (nightEntity != null) {
            nightEntity.getRoomRate();
            return nightEntity;
        } else {
            throw new NightNotFoundException("Night ID " + nightId + " does not exist!");
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
