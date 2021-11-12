/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomEntity;
import entity.RoomTypeEntity;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;
import util.exception.DeleteRoomException;
import util.exception.InputDataValidationException;
import util.exception.RoomTypeNotFoundException;
import util.exception.RoomNumberExistException;
import util.exception.RoomNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomException;

@Remote
public interface RoomSessionBeanRemote {
    
    public RoomEntity createNewRoom(RoomEntity newRoomEntity, String roomTypeName) throws RoomNumberExistException, UnknownPersistenceException, InputDataValidationException, RoomTypeNotFoundException;
    public List<RoomEntity> retrieveAllRooms();
    public RoomEntity retrieveRoomByRoomId(Long roomId) throws RoomNotFoundException;
    public RoomEntity retrieveRoomByRoomNumber(String number) throws RoomNotFoundException;
    public RoomEntity updateRoom(RoomEntity roomEntity) throws RoomNotFoundException, UpdateRoomException, InputDataValidationException;
    public void deleteRoom(Long roomId) throws RoomNotFoundException, DeleteRoomException;
    public List<RoomEntity> retreiveAvailableRooms(Date allocateDate);
}
