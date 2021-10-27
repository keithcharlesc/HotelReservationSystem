/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import util.enumeration.ReservationTypeEnum;

/**
 *
 * @author keithcharleschan
 */
@Entity
public class ReservationEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;
    
    @Column(nullable = false, precision = 11, scale = 2)
    private BigDecimal reservationFee;
    @Column(nullable = false)
    private LocalDateTime startDate;
    @Column(nullable = false)
    private LocalDateTime endDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationTypeEnum reservationType;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(nullable = false)
    private GuestEntity guest;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(nullable = false)
    private RoomTypeEntity roomType; 
    
    @OneToMany //Unidirectional (don't need mappedBy)
    @JoinColumn(nullable = false)
    private List<NightEntity> nights;
    
    @OneToMany(mappedBy="reservation")
    @JoinColumn(nullable = false)
    private List<ReservationRoomEntity> reservationRooms;

    public ReservationEntity() {
        this.nights = new ArrayList<>();
        this.reservationRooms = new ArrayList<>();
    }

    public ReservationEntity(BigDecimal reservationFee, LocalDateTime startDate, LocalDateTime endDate, ReservationTypeEnum reservationType) {
        this();
        this.reservationFee = reservationFee;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reservationType = reservationType;
    }
    
    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }
    
    public BigDecimal getReservationFee() {
        return reservationFee;
    }

    public void setReservationFee(BigDecimal reservationFee) {
        this.reservationFee = reservationFee;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public ReservationTypeEnum getReservationType() {
        return reservationType;
    }

    public void setReservationType(ReservationTypeEnum reservationType) {
        this.reservationType = reservationType;
    }

    public GuestEntity getGuest() {
        return guest;
    }

    public void setGuest(GuestEntity guest) {
        this.guest = guest;
    }

    public RoomTypeEntity getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomTypeEntity roomType) {
        this.roomType = roomType;
    }

    public List<NightEntity> getNights() {
        return nights;
    }

    public void setNights(List<NightEntity> nights) {
        this.nights = nights;
    }

    public List<ReservationRoomEntity> getReservationRoom() {
        return reservationRooms;
    }

    public void setReservationRoom(List<ReservationRoomEntity> reservationRooms) {
        this.reservationRooms = reservationRooms;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reservationId != null ? reservationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the reservationId fields are not set
        if (!(object instanceof ReservationEntity)) {
            return false;
        }
        ReservationEntity other = (ReservationEntity) object;
        if ((this.reservationId == null && other.reservationId != null) || (this.reservationId != null && !this.reservationId.equals(other.reservationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ReservationEntity[ id=" + reservationId + " ]";
    }
    
}
