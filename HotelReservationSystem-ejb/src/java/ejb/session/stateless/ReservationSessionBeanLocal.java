/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.ReservationEntity;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import util.exception.GuestNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.ReservationNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author keithcharleschan
 */
@Local
public interface ReservationSessionBeanLocal {

    public ReservationEntity createNewReservation(Long guestId, ReservationEntity newReservationEntity) throws GuestNotFoundException, InputDataValidationException, UnknownPersistenceException;
    
    public List<ReservationEntity> retrieveAllReservations();
   
    public ReservationEntity retrieveReservationByReservationId(Long reservationId) throws ReservationNotFoundException;
    
    public List<ReservationEntity> retrieveCurrentDayReservations(Date currentDate);
    
    public Date convertToDateViaSqlTimestamp(LocalDateTime dateToConvert);
}
