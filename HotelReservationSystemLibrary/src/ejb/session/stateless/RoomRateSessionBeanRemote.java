/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomRateEntity;
import java.util.List;
import javax.ejb.Remote;
import util.exception.DeleteRoomRateException;
import util.exception.InputDataValidationException;
import util.exception.RoomRateNameExistException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomRateException;

@Remote
public interface RoomRateSessionBeanRemote {

    public void createNewRoomRate(RoomRateEntity newRoomRateEntity, String roomTypeName) throws RoomRateNameExistException, UnknownPersistenceException, InputDataValidationException, RoomTypeNotFoundException;

    public List<RoomRateEntity> retrieveAllRoomRates();

    public RoomRateEntity retrieveRoomRateByRoomRateId(Long roomRateId) throws RoomRateNotFoundException;

    public RoomRateEntity retrieveRoomRateByRoomRateName(String name) throws RoomRateNotFoundException;

    public void updateRoomRate(RoomRateEntity roomRateEntity) throws RoomRateNotFoundException, UpdateRoomRateException, InputDataValidationException;

    public void deleteRoomRate(Long roomRateId) throws RoomRateNotFoundException, DeleteRoomRateException;

}
