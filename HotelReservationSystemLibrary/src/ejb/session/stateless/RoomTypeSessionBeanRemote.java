/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomTypeEntity;
import java.util.List;
import javax.ejb.Remote;
import util.exception.DeleteRoomTypeException;
import util.exception.InputDataValidationException;
import util.exception.RoomTypeNameExistException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomTypeException;

/**
 *
 * @author xianhui
 */
@Remote
public interface RoomTypeSessionBeanRemote {
    
    public RoomTypeEntity createNewRoomType(RoomTypeEntity newRoomTypeEntity) throws RoomTypeNameExistException, UnknownPersistenceException, InputDataValidationException;
    public List<RoomTypeEntity> retrieveAllRoomTypes();
    public RoomTypeEntity retrieveRoomTypeByRoomTypeId(Long roomTypeId) throws RoomTypeNotFoundException;
    public RoomTypeEntity retrieveRoomTypeByRoomTypeName(String name) throws RoomTypeNotFoundException;
    public void updateRoomType(RoomTypeEntity roomTypeEntity) throws RoomTypeNotFoundException, UpdateRoomTypeException, InputDataValidationException;
    public void deleteRoomType(Long roomTypeId) throws RoomTypeNotFoundException, DeleteRoomTypeException;
    
}
