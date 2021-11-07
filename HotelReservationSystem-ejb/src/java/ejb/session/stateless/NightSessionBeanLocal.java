/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.NightEntity;
import java.util.List;
import javax.ejb.Local;
import util.exception.InputDataValidationException;
import util.exception.NightNotFoundException;
import util.exception.RoomRateNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author xianhui
 */
@Local
public interface NightSessionBeanLocal {
    public NightEntity createNewNight(NightEntity newNightEntity, String roomRateName) throws UnknownPersistenceException, InputDataValidationException, RoomRateNotFoundException;
    public List<NightEntity> retrieveAllNights();
    public NightEntity retrieveNightByNightId(Long nightId) throws NightNotFoundException;
}
