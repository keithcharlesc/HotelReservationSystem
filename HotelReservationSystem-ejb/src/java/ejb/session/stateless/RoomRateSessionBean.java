/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.PeakRateEntity;
import entity.PromotionRateEntity;
import entity.RoomRateEntity;
import entity.RoomTypeEntity;
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
import util.exception.DeleteRoomRateException;
import util.exception.InputDataValidationException;
import util.exception.RoomRateNameExistException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomRateException;

/**
 *
 * @author xianhui
 */
@Stateless
public class RoomRateSessionBean implements RoomRateSessionBeanRemote, RoomRateSessionBeanLocal {

    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBeanLocal;

    @EJB
    private NightSessionBeanLocal nightSessionBean;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public RoomRateSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public void createNewRoomRate(RoomRateEntity newRoomRateEntity, String roomTypeName) throws RoomRateNameExistException, UnknownPersistenceException, InputDataValidationException, RoomTypeNotFoundException {
        Set<ConstraintViolation<RoomRateEntity>> constraintViolations = validator.validate(newRoomRateEntity);

        if (constraintViolations.isEmpty()) {
            try {
                RoomTypeEntity roomType = roomTypeSessionBeanLocal.retrieveRoomTypeByRoomTypeName(roomTypeName);
                newRoomRateEntity.setRoomType(roomType);
                roomType.getRoomRates().add(newRoomRateEntity);
                em.persist(newRoomRateEntity);
                System.out.println("Successfully Created!");
                em.flush();
//                return newRoomRateEntity.getRoomRateId();
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new RoomRateNameExistException();
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } catch (RoomTypeNotFoundException ex) {
                throw new RoomTypeNotFoundException(ex.getMessage());
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    @Override
    public List<RoomRateEntity> retrieveAllRoomRates() {
        Query query = em.createQuery("SELECT rr FROM RoomRateEntity rr WHERE rr.isDisabled = :disabled");
        query.setParameter("disabled", false);

        return query.getResultList();
    }

    @Override
    public RoomRateEntity retrieveRoomRateByRoomRateId(Long roomRateId) throws RoomRateNotFoundException {
        RoomRateEntity roomRateEntity = em.find(RoomRateEntity.class, roomRateId);

        if (roomRateEntity != null) {
            roomRateEntity.getRoomType();
            roomRateEntity.getNights();
            return roomRateEntity;
        } else {
            throw new RoomRateNotFoundException("RoomRate ID " + roomRateId + " does not exist!");
        }
    }

    @Override
    public RoomRateEntity retrieveRoomRateByRoomRateName(String name) throws RoomRateNotFoundException {
        Query query = em.createQuery("SELECT rr FROM RoomRateEntity rr WHERE rr.name = :inName AND rr.isDisabled = :disabled");
        query.setParameter("inName", name);
        query.setParameter("disabled", false);

        try {
            return (RoomRateEntity) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new RoomRateNotFoundException("RoomRate name " + name + " does not exist!");
        }
    }

//    @Override
//    public void updateRoomRate(RoomRateEntity roomRateEntity) throws RoomRateNotFoundException, UpdateRoomRateException, InputDataValidationException {
//        if (roomRateEntity != null && roomRateEntity.getRoomRateId() != null) {
//            Set<ConstraintViolation<RoomRateEntity>> constraintViolations = validator.validate(roomRateEntity);
//
//            if (constraintViolations.isEmpty()) {
//                RoomRateEntity roomRateEntityToUpdate = retrieveRoomRateByRoomRateId(roomRateEntity.getRoomRateId());
//                if (roomRateEntity instanceof PromotionRateEntity) {
//                    PromotionRateEntity promoRateEntity = (PromotionRateEntity) roomRateEntity;
//                    PromotionRateEntity promoRateEntityToUpdate = (PromotionRateEntity) roomRateEntityToUpdate;
//                    promoRateEntityToUpdate.setName(promoRateEntity.getName());
//                    promoRateEntityToUpdate.setRatePerNight(promoRateEntity.getRatePerNight());
//                    promoRateEntityToUpdate.setStartDate(promoRateEntity.getStartDate());
//                    promoRateEntityToUpdate.setEndDate(promoRateEntity.getEndDate());
//                    promoRateEntityToUpdate.setIsDisabled(promoRateEntity.getIsDisabled());
//                } else if (roomRateEntity instanceof PeakRateEntity) {
//                    PeakRateEntity peakRateEntity = (PeakRateEntity) roomRateEntity;
//                    PeakRateEntity peakRateEntityToUpdate = (PeakRateEntity) roomRateEntityToUpdate;
//                    peakRateEntityToUpdate.setName(peakRateEntity.getName());
//                    peakRateEntityToUpdate.setRatePerNight(peakRateEntity.getRatePerNight());
//                    peakRateEntityToUpdate.setStartDate(peakRateEntity.getStartDate());
//                    peakRateEntityToUpdate.setEndDate(peakRateEntity.getEndDate());
//                    peakRateEntityToUpdate.setIsDisabled(peakRateEntity.getIsDisabled());
//                } else {
//                    roomRateEntityToUpdate.setName(roomRateEntity.getName());
//                    roomRateEntityToUpdate.setRatePerNight(roomRateEntity.getRatePerNight());
//                    roomRateEntityToUpdate.setIsDisabled(roomRateEntity.getIsDisabled());
//                }
//            } else {
//                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
//            }
//        } else {
//            throw new RoomRateNotFoundException("RoomRate ID not provided for roomRate to be updated");
//        }
//    }
    
    @Override
    public void updatePromotionRate(PromotionRateEntity promoRateEntity) throws RoomRateNotFoundException, UpdateRoomRateException, InputDataValidationException {
        if (promoRateEntity != null && promoRateEntity.getRoomRateId() != null) {
            Set<ConstraintViolation<RoomRateEntity>> constraintViolations = validator.validate(promoRateEntity);

            if (constraintViolations.isEmpty()) {
                PromotionRateEntity promoRateEntityToUpdate = (PromotionRateEntity) retrieveRoomRateByRoomRateId(promoRateEntity.getRoomRateId());

                promoRateEntityToUpdate.setName(promoRateEntity.getName());
                promoRateEntityToUpdate.setRatePerNight(promoRateEntity.getRatePerNight());
                promoRateEntityToUpdate.setStartDate(promoRateEntity.getStartDate());
                promoRateEntityToUpdate.setEndDate(promoRateEntity.getEndDate());
                promoRateEntityToUpdate.setIsDisabled(promoRateEntity.getIsDisabled());
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } else {
            throw new RoomRateNotFoundException("RoomRate ID not provided for roomRate to be updated");
        }
    }
    
    @Override
    public void updatePeakRate(PeakRateEntity peakRateEntity) throws RoomRateNotFoundException, UpdateRoomRateException, InputDataValidationException {
        if (peakRateEntity != null && peakRateEntity.getRoomRateId() != null) {
            Set<ConstraintViolation<RoomRateEntity>> constraintViolations = validator.validate(peakRateEntity);

            if (constraintViolations.isEmpty()) {

                PeakRateEntity peakRateEntityToUpdate = (PeakRateEntity) retrieveRoomRateByRoomRateId(peakRateEntity.getRoomRateId());
                peakRateEntityToUpdate.setName(peakRateEntity.getName());
                peakRateEntityToUpdate.setRatePerNight(peakRateEntity.getRatePerNight());
                peakRateEntityToUpdate.setStartDate(peakRateEntity.getStartDate());
                peakRateEntityToUpdate.setEndDate(peakRateEntity.getEndDate());
                peakRateEntityToUpdate.setIsDisabled(peakRateEntity.getIsDisabled());
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } else {
            throw new RoomRateNotFoundException("RoomRate ID not provided for roomRate to be updated");
        }
    }
    
    @Override
    public void updateRoomRate(RoomRateEntity roomRateEntity) throws RoomRateNotFoundException, UpdateRoomRateException, InputDataValidationException {
        if (roomRateEntity != null && roomRateEntity.getRoomRateId() != null) {
            Set<ConstraintViolation<RoomRateEntity>> constraintViolations = validator.validate(roomRateEntity);

            if (constraintViolations.isEmpty()) {

                RoomRateEntity roomRateEntityToUpdate = retrieveRoomRateByRoomRateId(roomRateEntity.getRoomRateId());
                roomRateEntityToUpdate.setName(roomRateEntity.getName());
                roomRateEntityToUpdate.setRatePerNight(roomRateEntity.getRatePerNight());
                roomRateEntityToUpdate.setIsDisabled(roomRateEntity.getIsDisabled());
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } else {
            throw new RoomRateNotFoundException("RoomRate ID not provided for roomRate to be updated");
        }
    }

    @Override
    public void deleteRoomRate(Long roomRateId) throws RoomRateNotFoundException, DeleteRoomRateException {

        try {
            RoomRateEntity roomRateEntity = retrieveRoomRateByRoomRateId(roomRateId);
            roomRateEntity.setIsDisabled(true);

        } catch (RoomRateNotFoundException ex) {
            throw new RoomRateNotFoundException("RoomRate ID" + roomRateId + " is not found!");
        }

//        if (roomRateEntity.getNights().isEmpty()) {
//            em.remove(roomRateEntity);
//        } else {
//            throw new DeleteRoomRateException("RoomRate ID " + roomRateId + " is associated with night(s) and cannot be deleted!");
//        }
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<RoomRateEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

}
