/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.NormalRateEntity;
import entity.PeakRateEntity;
import entity.PromotionRateEntity;
import entity.PublishedRateEntity;
import entity.RoomRateEntity;
import java.util.Date;
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

    public void updatePeakRate(PeakRateEntity peakRateEntity) throws RoomRateNotFoundException, UpdateRoomRateException, InputDataValidationException;

    public void updatePromotionRate(PromotionRateEntity promoRateEntity) throws RoomRateNotFoundException, UpdateRoomRateException, InputDataValidationException;

    public PromotionRateEntity retrievePromotionRateByRoomTypeAndValidityPeriod(Long roomTypeId, Date date) throws RoomRateNotFoundException;

    public PeakRateEntity retrievePeakRateByRoomTypeAndValidityPeriod(Long roomTypeId, Date date) throws RoomRateNotFoundException;

    public NormalRateEntity retrieveNormalRateByRoomType(Long roomTypeId) throws RoomRateNotFoundException;
    
    public PublishedRateEntity retrievePublishedRateByRoomType(Long roomTypeId) throws RoomRateNotFoundException;
}
