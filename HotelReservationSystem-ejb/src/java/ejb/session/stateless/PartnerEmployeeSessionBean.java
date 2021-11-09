package ejb.session.stateless;

import entity.PartnerEmployeeEntity;
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
import util.exception.DeletePartnerEmployeeException;
import util.exception.PartnerNotFoundException;
import util.exception.PartnerUsernameExistException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdatePartnerEmployeeException;

/**
 *
 * @author xianhui
 */
@Stateless
public class PartnerEmployeeSessionBean implements PartnerEmployeeSessionBeanRemote, PartnerEmployeeSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public PartnerEmployeeSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }


    @Override
    public Long createNewPartnerEmployee(PartnerEmployeeEntity newPartnerEmployeeEntity) throws PartnerUsernameExistException, UnknownPersistenceException, InputDataValidationException {
        Set<ConstraintViolation<PartnerEmployeeEntity>> constraintViolations = validator.validate(newPartnerEmployeeEntity);

        if (constraintViolations.isEmpty()) {
            try {
                em.persist(newPartnerEmployeeEntity);
                em.flush();

                return newPartnerEmployeeEntity.getPartnerId();
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new PartnerUsernameExistException();
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
    public List<PartnerEmployeeEntity> retrieveAllPartnerEmployees() {
        Query query = em.createQuery("SELECT p FROM PartnerEmployeeEntity p ORDER BY p.name");

        return query.getResultList();
    }

    @Override
    public PartnerEmployeeEntity retrievePartnerEmployeeByPartnerEmployeeId(Long partnerEmployeeId) throws PartnerNotFoundException {
        PartnerEmployeeEntity partnerEmployeeEntity = em.find(PartnerEmployeeEntity.class, partnerEmployeeId);

        if (partnerEmployeeEntity != null) {
            partnerEmployeeEntity.getReservations().size();
            return partnerEmployeeEntity;
        } else {
            throw new PartnerNotFoundException("PartnerEmployee ID " + partnerEmployeeId + " does not exist!");
        }
    }

    @Override
    public PartnerEmployeeEntity retrievePartnerEmployeeByUsername(String username) throws PartnerNotFoundException {
        Query query = em.createQuery("SELECT p FROM PartnerEmployeeEntity p WHERE p.username = :inUsername");
        query.setParameter("inUsername", username);

        try {
            PartnerEmployeeEntity partner = (PartnerEmployeeEntity) query.getSingleResult();
            partner.getReservations().size();
            return partner;
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new PartnerNotFoundException("PartnerEmployee Username " + username + " does not exist!");
        }
    }

    @Override
    public PartnerEmployeeEntity partnerEmployeeLogin(String username, String password) throws InvalidLoginCredentialException {
        try {
            PartnerEmployeeEntity partnerEmployeeEntity = retrievePartnerEmployeeByUsername(username);

            if (partnerEmployeeEntity.getPassword().equals(password)) {
                return partnerEmployeeEntity;
            } else {
                throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
            }
        } catch (PartnerNotFoundException ex) {
            throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
        }
    }

    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<PartnerEmployeeEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}

