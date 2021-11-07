package ejb.session.stateless;

import entity.CustomerEntity;
import entity.GuestEntity;
import java.util.List;
import javax.ejb.Remote;
import util.exception.DeleteGuestException;
import util.exception.GuestEmailExistException;
import util.exception.GuestNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;

@Remote
public interface GuestSessionBeanRemote {

    public Long createNewGuest(GuestEntity newGuestEntity) throws GuestEmailExistException, UnknownPersistenceException, InputDataValidationException;

    public List<GuestEntity> retrieveAllGuests();

    public GuestEntity retrieveGuestByGuestId(Long guestId) throws GuestNotFoundException;

    public GuestEntity retrieveGuestByEmail(String email) throws GuestNotFoundException;

    public CustomerEntity guestLogin(String email, String password) throws InvalidLoginCredentialException;

    public void deleteGuest(Long guestId) throws GuestNotFoundException, DeleteGuestException;
}
