/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.PartnerEmployeeEntity;
import java.util.List;
import javax.ejb.Local;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.PartnerEmployeeNotFoundException;
import util.exception.PartnerEmployeeUsernameExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author xianhui
 */
@Local
public interface PartnerEmployeeSessionBeanLocal {
    public Long createNewPartnerEmployee(PartnerEmployeeEntity newPartnerEmployeeEntity) throws PartnerEmployeeUsernameExistException, UnknownPersistenceException, InputDataValidationException;
    public List<PartnerEmployeeEntity> retrieveAllPartnerEmployees();
    public PartnerEmployeeEntity retrievePartnerEmployeeByPartnerEmployeeId(Long partnerEmployeeId) throws PartnerEmployeeNotFoundException;
    public PartnerEmployeeEntity retrievePartnerEmployeeByUsername(String username) throws PartnerEmployeeNotFoundException;
    public PartnerEmployeeEntity partnerEmployeeLogin(String username, String password) throws InvalidLoginCredentialException;
}
