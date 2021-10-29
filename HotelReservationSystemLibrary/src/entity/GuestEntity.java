/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class GuestEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long guestId;
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(min = 4, max = 32)
    private String guestName;
    @Column(nullable = false, unique = true, length = 64)
    @NotNull
    @Size(min = 4, max = 64)
    private String email;
    @Column(nullable = false) //maybe can limit smaller
    @NotNull
    private Long phoneNo;

    @OneToMany(mappedBy = "guest")
    private List<ReservationEntity> reservations;

    public GuestEntity() {
        this.reservations = new ArrayList<>();
    }

    public GuestEntity(String guestName, String email, Long phoneNo) {
        this();
        this.guestName = guestName;
        this.email = email;
        this.phoneNo = phoneNo;
    }

    public Long getGuestId() {
        return guestId;
    }

    public void setGuestId(Long guestId) {
        this.guestId = guestId;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(Long phoneNo) {
        this.phoneNo = phoneNo;
    }

    public List<ReservationEntity> getReservations() {
        return reservations;
    }

    public void setReservations(List<ReservationEntity> reservations) {
        this.reservations = reservations;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the guestId fields are not set
        if (!(object instanceof GuestEntity)) {
            return false;
        }
        GuestEntity other = (GuestEntity) object;
        if ((this.getGuestId() == null && other.getGuestId() != null) || (this.getGuestId() != null && !this.guestId.equals(other.guestId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "GuestEntity{" + "guestId=" + guestId + ", guestName=" + guestName + ", email=" + email + ", phoneNo=" + phoneNo + ", reservations=" + reservations + '}';
    }

}
