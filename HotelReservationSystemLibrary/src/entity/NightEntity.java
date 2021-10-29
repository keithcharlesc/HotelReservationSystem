/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class NightEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long nightId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private RoomRateEntity roomRate;
    
    @Column(nullable = false)
    @NotNull
    private LocalDateTime date;

    public NightEntity() {
    }

    public NightEntity(RoomRateEntity roomRate, LocalDateTime date) {
        this.roomRate = roomRate;
        this.date = date;
    }

    public Long getNightId() {
        return nightId;
    }

    public void setNightId(Long nightId) {
        this.nightId = nightId;
    }

    public RoomRateEntity getRoomRate() {
        return roomRate;
    }

    public void setRoomRate(RoomRateEntity roomRate) {
        this.roomRate = roomRate;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the nightId fields are not set
        if (!(object instanceof NightEntity)) {
            return false;
        }
        NightEntity other = (NightEntity) object;
        if ((this.getNightId() == null && other.getNightId() != null) || (this.getNightId() != null && !this.nightId.equals(other.nightId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "NightEntity{" + "nightId=" + getNightId() + ", roomRate=" + getRoomRate() + ", date=" + getDate() + '}';
    }

}
