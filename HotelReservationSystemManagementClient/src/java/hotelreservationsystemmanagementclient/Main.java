package hotelreservationsystemmanagementclient;

import javax.ejb.EJB;
import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.ExceptionRecordSessionBeanRemote;
import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.NightSessionBeanRemote;
import ejb.session.stateless.PartnerEmployeeSessionBeanRemote;
import ejb.session.stateless.ReservationRoomSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;

public class Main {

    @EJB
    private static ReservationSessionBeanRemote reservationSessionBean;
    @EJB
    private static RoomRateSessionBeanRemote roomRateSessionBean;
    @EJB
    private static RoomSessionBeanRemote roomSessionBean;
    @EJB
    private static RoomTypeSessionBeanRemote roomTypeSessionBean;
    @EJB
    private static ReservationRoomSessionBeanRemote reservationRoomSessionBean;
    @EJB
    private static NightSessionBeanRemote nightSessionBean;
    @EJB
    private static ExceptionRecordSessionBeanRemote exceptionRecordSessionBean;
    @EJB
    private static EmployeeSessionBeanRemote employeeSessionBean;
    @EJB
    private static PartnerEmployeeSessionBeanRemote partnerEmployeeSessionBean;
    @EJB
    private static GuestSessionBeanRemote guestSessionBean;

    public static void main(String[] args) {
        MainApp mainApp = new MainApp(reservationSessionBean, roomRateSessionBean, roomSessionBean, roomTypeSessionBean, reservationRoomSessionBean, nightSessionBean, exceptionRecordSessionBean, employeeSessionBean, partnerEmployeeSessionBean, guestSessionBean);
        mainApp.runApp();
    }

}
