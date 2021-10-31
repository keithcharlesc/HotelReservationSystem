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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import util.enumeration.RoomStatusEnum;

@Entity
public class RoomEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private RoomStatusEnum roomStatusEnum;
    @Column(nullable = false)
    @NotNull
    private Boolean roomAllocated;
    @Column(nullable = false)
    @NotNull
    private Boolean isDisabled;

    @ManyToOne(fetch = FetchType.LAZY, optional=false)
    @JoinColumn(nullable = false)
    private RoomTypeEntity roomType;

    @OneToMany(mappedBy = "room")
    private List<ReservationRoomEntity> reservationRooms;

    public RoomEntity() {
        this.reservationRooms = new ArrayList<>();
    }

    public RoomEntity(RoomStatusEnum roomStatusEnum) {
        this();
        this.roomStatusEnum = roomStatusEnum;
        this.roomAllocated = false;
        this.isDisabled = false;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public RoomStatusEnum getRoomStatusEnum() {
        return roomStatusEnum;
    }

    public void setRoomStatusEnum(RoomStatusEnum roomStatusEnum) {
        this.roomStatusEnum = roomStatusEnum;
    }

    public Boolean getRoomAllocated() {
        return roomAllocated;
    }

    public void setRoomAllocated(Boolean roomAllocated) {
        this.roomAllocated = roomAllocated;
    }

    public Boolean getIsDisabled() {
        return isDisabled;
    }

    public void setIsDisabled(Boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    public RoomTypeEntity getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomTypeEntity roomType) {
        this.roomType = roomType;
    }

    public List<ReservationRoomEntity> getReservationRooms() {
        return reservationRooms;
    }

    public void setReservationRooms(List<ReservationRoomEntity> reservationRooms) {
        this.reservationRooms = reservationRooms;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the roomId fields are not set
        if (!(object instanceof RoomEntity)) {
            return false;
        }
        RoomEntity other = (RoomEntity) object;
        if ((this.getRoomId() == null && other.getRoomId() != null) || (this.getRoomId() != null && !this.roomId.equals(other.roomId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "RoomEntity{" + "roomId=" + getRoomId() + ", roomStatusEnum=" + getRoomStatusEnum() + ", roomAllocated=" + getRoomAllocated() + ", isDisabled=" + getIsDisabled() + ", roomType=" + getRoomType() + ", reservationRooms=" + getReservationRooms() + '}';
    }

}
