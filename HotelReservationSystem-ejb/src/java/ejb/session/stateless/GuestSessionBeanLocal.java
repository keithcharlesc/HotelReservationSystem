package ejb.session.stateless;

import entity.CustomerEntity;
import entity.GuestEntity;
import entity.ReservationEntity;
import java.util.List;
import javax.ejb.Local;
import util.exception.DeleteGuestException;
import util.exception.GuestEmailExistException;
import util.exception.GuestNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.RetrieveGuestReservationsException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author keithcharleschan
 */
@Local
public interface GuestSessionBeanLocal {

    public Long createNewGuest(GuestEntity newGuestEntity) throws GuestEmailExistException, UnknownPersistenceException, InputDataValidationException;

    public List<GuestEntity> retrieveAllGuests();

    public GuestEntity retrieveGuestByGuestId(Long guestId) throws GuestNotFoundException;

    public GuestEntity retrieveGuestByEmail(String email) throws GuestNotFoundException;

    public CustomerEntity guestLogin(String email, String password) throws InvalidLoginCredentialException;

    public void deleteGuest(Long guestId) throws GuestNotFoundException, DeleteGuestException;

    
}
