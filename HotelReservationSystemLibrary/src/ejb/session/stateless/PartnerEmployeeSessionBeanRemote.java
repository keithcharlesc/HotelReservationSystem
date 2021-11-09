/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.PartnerEmployeeEntity;
import java.util.List;
import javax.ejb.Remote;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.PartnerNotFoundException;
import util.exception.PartnerUsernameExistException;
import util.exception.UnknownPersistenceException;

@Remote
public interface PartnerEmployeeSessionBeanRemote {
    public Long createNewPartnerEmployee(PartnerEmployeeEntity newPartnerEmployeeEntity) throws PartnerUsernameExistException, UnknownPersistenceException, InputDataValidationException;
    public List<PartnerEmployeeEntity> retrieveAllPartnerEmployees();
    public PartnerEmployeeEntity retrievePartnerEmployeeByPartnerEmployeeId(Long partnerEmployeeId) throws PartnerNotFoundException;
    public PartnerEmployeeEntity retrievePartnerEmployeeByUsername(String username) throws PartnerNotFoundException;
    public PartnerEmployeeEntity partnerEmployeeLogin(String username, String password) throws InvalidLoginCredentialException;
}
