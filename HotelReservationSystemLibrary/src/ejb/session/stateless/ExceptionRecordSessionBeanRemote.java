/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.ExceptionRecordEntity;
import java.util.List;
import javax.ejb.Remote;
import util.exception.ExceptionRecordNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.ReservationRoomNotFoundException;
import util.exception.UnknownPersistenceException;

@Remote
public interface ExceptionRecordSessionBeanRemote {

    public ExceptionRecordEntity createNewExceptionRecord(ExceptionRecordEntity newExceptionRecordEntity, Long reservationRoomId) throws UnknownPersistenceException, InputDataValidationException, ReservationRoomNotFoundException;

    public List<ExceptionRecordEntity> retrieveAllExceptionRecords();

    public List<ExceptionRecordEntity> retrieveUnresolvedExceptionRecords();

    public ExceptionRecordEntity retrieveExceptionRecordByExceptionRecordId(Long exceptionRecordId) throws ExceptionRecordNotFoundException;

    public void updateExceptionRecord(ExceptionRecordEntity exceptionRecordEntity) throws ExceptionRecordNotFoundException, InputDataValidationException;

}
