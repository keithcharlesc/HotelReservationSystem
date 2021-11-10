/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import util.enumeration.ReservationTypeEnum;

@Entity
public class ReservationEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    @Column(nullable = false)
    @NotNull
    @Min(1)
    private Integer numberOfRooms;

    @Column(nullable = false, precision = 11, scale = 2)
    @NotNull
    @Digits(integer = 11, fraction = 2)
    private BigDecimal reservationFee;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @NotNull
    private Date startDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @Future
    @NotNull
    private Date endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private ReservationTypeEnum reservationType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private GuestEntity guest;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private RoomTypeEntity roomType;

    @OneToMany(targetEntity = NightEntity.class)
//    @JoinColumn(nullable = false) 
    private List<NightEntity> nights;

    @OneToMany(mappedBy = "reservation")
    private List<ReservationRoomEntity> reservationRooms;

    public ReservationEntity() {
        this.nights = new ArrayList<>();
        this.reservationRooms = new ArrayList<>();
    }

    public ReservationEntity(Integer numberOfRooms, BigDecimal reservationFee, Date startDate, Date endDate, ReservationTypeEnum reservationType) {
        this();
        this.numberOfRooms = numberOfRooms;
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

    public Integer getNumberOfRooms() {
        return numberOfRooms;
    }

    public void setNumberOfRooms(Integer numberOfRooms) {
        this.numberOfRooms = numberOfRooms;
    }

    public BigDecimal getReservationFee() {
        return reservationFee;
    }

    public void setReservationFee(BigDecimal reservationFee) {
        this.reservationFee = reservationFee;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
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

    public List<ReservationRoomEntity> getReservationRooms() {
        return reservationRooms;
    }

    public void setReservationRooms(List<ReservationRoomEntity> reservationRooms) {
        this.reservationRooms = reservationRooms;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the reservationId fields are not set
        if (!(object instanceof ReservationEntity)) {
            return false;
        }
        ReservationEntity other = (ReservationEntity) object;
        if ((this.getReservationId() == null && other.getReservationId() != null) || (this.getReservationId() != null && !this.reservationId.equals(other.reservationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ReservationEntity{" + "reservationId=" + reservationId + ", numberOfRooms=" + numberOfRooms + ", reservationFee=" + reservationFee + ", startDate=" + startDate + ", endDate=" + endDate + ", reservationType=" + reservationType + ", guest=" + guest + ", roomType=" + roomType + ", nights=" + nights + ", reservationRooms=" + reservationRooms + '}';
    }

}
