/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.PartnerEmployeeSessionBeanLocal;
import ejb.session.stateless.ReservationSessionBeanLocal;
import entity.PartnerEmployeeEntity;
import entity.ReservationEntity;
import java.text.NumberFormat;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.InvalidLoginCredentialException;
import util.exception.PartnerNotFoundException;
import util.exception.ReservationNotFoundException;

/**
 *
 * @author keithcharleschan
 */
@WebService(serviceName = "HotelReservationSystemWebService")
@Stateless()
public class HotelReservationSystemWebService {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    @EJB
    private ReservationSessionBeanLocal reservationSessionBeanLocal;

    @EJB
    private PartnerEmployeeSessionBeanLocal partnerEmployeeSessionBeanLocal;

    /**
     * This is a sample web service operation
     */
    
    @WebMethod(operationName = "partnerEmployeeLogin")
    public PartnerEmployeeEntity partnerEmployeeLogin(@WebParam(name = "username") String username,
                                                        @WebParam(name = "password") String password) throws InvalidLoginCredentialException {
        return partnerEmployeeSessionBeanLocal.partnerEmployeeLogin(username, password);
    }
    
    //need detach ??? 
    @WebMethod(operationName = "viewPartnerEmployeeReservations")
    public void viewAllPartnerEmployeeReservations(@WebParam(name = "username") String username) throws PartnerNotFoundException {
        PartnerEmployeeEntity partner = partnerEmployeeSessionBeanLocal.retrieveAllPartnerReservations(username);
        System.out.printf("%30s%30s%30s%30s%30s%30s%30s\n", "Reservation ID", "Guest Name", "Reservation Room Type", "Number of Rooms", "Start Date", "End Date", "Reservation Fee");
        for(ReservationEntity reservation: partner.getReservations()) {
            System.out.printf("%30s%30s%30s%30s%30s%30s%30s\n", reservation.getReservationId(), reservation.getGuest().getName(), reservation.getRoomType().getRoomTypeName(), reservation.getNumberOfRooms(), reservation.getStartDate().toString(), reservation.getEndDate().toString(), NumberFormat.getCurrencyInstance().format(reservation.getReservationFee()));
        }
    }
    
    @WebMethod(operationName = "viewPartnerEmployeeReservationDetails")
    public void viewPartnerEmployeeReservationDetails(@WebParam(name = "reservationId") Long reservationId) throws ReservationNotFoundException {
        ReservationEntity reservation = reservationSessionBeanLocal.retrieveReservationByReservationId(reservationId);
        System.out.printf("%30s%30s%30s%30s%30s%30s\n", "", "Guest Name", "Reservation Room Type", "Number of Rooms", "Start Date", "End Date", "Reservation Fee");
        System.out.printf("%30s%30s%30s%30s%30s%30s\n", reservation.getGuest().getName(), reservation.getRoomType().getRoomTypeName(), reservation.getNumberOfRooms(), reservation.getStartDate().toString(), reservation.getEndDate().toString(), NumberFormat.getCurrencyInstance().format(reservation.getReservationFee()));
    }

    public void persist(Object object) {
        em.persist(object);
    }
}
