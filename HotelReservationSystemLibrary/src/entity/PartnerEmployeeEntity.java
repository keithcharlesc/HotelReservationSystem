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
import javax.persistence.OneToMany;

/**
 *
 * @author xianhui
 */
@Entity
public class PartnerEmployeeEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long partnerId;
    @Column(nullable = false, length = 32)
    private String name;
    @Column(nullable = false, unique = true, length = 32)
    private String username;
    @Column(nullable = false, length = 32)
    private String password;

    @OneToMany
    private List<ReservationEntity> reservations;

    public PartnerEmployeeEntity(String name, String username, String password) {
        this();
        this.name = name;
        this.username = username;
        this.password = password;
        this.reservations = reservations;
    }

    public PartnerEmployeeEntity() {
        this.reservations = new ArrayList<>();
    }

    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<ReservationEntity> getReservations() {
        return reservations;
    }

    public void setReservations(List<ReservationEntity> reservations) {
        this.reservations = reservations;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the partnerId fields are not set
        if (!(object instanceof PartnerEmployeeEntity)) {
            return false;
        }
        PartnerEmployeeEntity other = (PartnerEmployeeEntity) object;
        if ((this.partnerId == null && other.partnerId != null) || (this.partnerId != null && !this.partnerId.equals(other.partnerId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PartnerEmployeeEntity{" + "partnerId=" + partnerId + ", name=" + name + ", username=" + username + ", password=" + password + ", reservations=" + reservations + '}';
    }


}
