/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

//WEAK ENTITY 
@Entity
public class ReservationRoomEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationRoomId;

    @Column(nullable = false)
    @NotNull
    private boolean isAllocated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private ReservationEntity reservation;

    @OneToOne(fetch = FetchType.LAZY) // can be null
    private ExceptionRecordEntity exceptionRecord;

    @ManyToOne(fetch = FetchType.LAZY) // can be null
    private RoomEntity room;

    public ReservationRoomEntity() {
        this.isAllocated = false;
    }

    public Long getReservationRoomId() {
        return reservationRoomId;
    }

    public void setReservationRoomId(Long reservationRoomId) {
        this.reservationRoomId = reservationRoomId;
    }

    public boolean getIsAllocated() {
        return isAllocated;
    }

    public void setIsAllocated(boolean isAllocated) {
        this.isAllocated = isAllocated;
    }

    public ReservationEntity getReservation() {
        return reservation;
    }

    public void setReservation(ReservationEntity reservation) {
        this.reservation = reservation;
    }

    public ExceptionRecordEntity getExceptionRecord() {
        return exceptionRecord;
    }

    public void setExceptionRecord(ExceptionRecordEntity exceptionRecord) {
        this.exceptionRecord = exceptionRecord;
    }

    public RoomEntity getRoom() {
        return room;
    }

    public void setRoom(RoomEntity room) {
        this.room = room;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the reservationRoomId fields are not set
        if (!(object instanceof ReservationRoomEntity)) {
            return false;
        }
        ReservationRoomEntity other = (ReservationRoomEntity) object;
        if ((this.getReservationRoomId() == null && other.getReservationRoomId() != null) || (this.getReservationRoomId() != null && !this.reservationRoomId.equals(other.reservationRoomId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ReservationRoomEntity{" + "reservationRoomId=" + reservationRoomId + ", isAllocated=" + isAllocated + ", reservation=" + reservation + ", exceptionRecord=" + exceptionRecord + ", room=" + room + '}';
    }

}
