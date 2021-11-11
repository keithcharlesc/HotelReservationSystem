/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.PartnerEmployeeSessionBeanLocal;
import ejb.session.stateless.ReservationSessionBeanLocal;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;

/**
 *
 * @author keithcharleschan
 */
@WebService(serviceName = "HotelReservationSystemWebService")
@Stateless()
public class HotelReservationSystemWebService {

    @EJB
    private ReservationSessionBeanLocal reservationSessionBeanLocal;

    @EJB
    private PartnerEmployeeSessionBeanLocal partnerEmployeeSessionBeanLocal;

    /**
     * This is a sample web service operation
     */
    
    @WebMethod(operationName = "createNewPartnerEmployee")
    public Long createNewPartnerEmployee(@WebParam(name = "name") String txt) {
        return 1L;
    }
}
