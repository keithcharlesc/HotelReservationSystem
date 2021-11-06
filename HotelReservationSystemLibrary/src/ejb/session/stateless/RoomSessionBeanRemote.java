/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomEntity;
import java.util.List;
import javax.ejb.Remote;
import util.exception.DeleteRoomException;
import util.exception.InputDataValidationException;
import util.exception.RoomTypeNotFoundException;
import util.exception.RoomNameExistException;
import util.exception.RoomNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomException;

/**
 *
 * @author xianhui
 */
@Remote
public interface RoomSessionBeanRemote {
    

    public RoomEntity createNewRoom(RoomEntity newRoomEntity, String roomTypeName) throws RoomNameExistException, UnknownPersistenceException, InputDataValidationException, RoomTypeNotFoundException;
    public List<RoomEntity> retrieveAllRooms();
    public RoomEntity retrieveRoomByRoomId(Long roomId) throws RoomNotFoundException;
    public RoomEntity retrieveRoomByRoomNumber(long number) throws RoomNotFoundException;
    public void updateRoom(RoomEntity roomEntity) throws RoomNotFoundException, UpdateRoomException, InputDataValidationException;
    public void deleteRoom(Long roomId) throws RoomNotFoundException, DeleteRoomException;
}
