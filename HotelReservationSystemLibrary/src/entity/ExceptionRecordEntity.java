/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 *
 * @author keithcharleschan
 */
@Entity
public class ExceptionRecordEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long exceptionRecordId;
    private int typeOfException;

    @OneToOne(fetch = FetchType.LAZY)
    private ReservationRoomEntity reservationRoom;

    public ExceptionRecordEntity() {
    }

    public ExceptionRecordEntity(int typeOfException) {
        this.typeOfException = typeOfException;
    }

    public Long getExceptionRecordId() {
        return exceptionRecordId;
    }

    public void setExceptionRecordId(Long exceptionRecordId) {
        this.exceptionRecordId = exceptionRecordId;
    }

    public int getTypeOfException() {
        return typeOfException;
    }

    public void setTypeOfException(int typeOfException) {
        this.typeOfException = typeOfException;
    }

    public ReservationRoomEntity getReservationRoom() {
        return reservationRoom;
    }

    public void setReservationRoom(ReservationRoomEntity reservationRoom) {
        this.reservationRoom = reservationRoom;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (exceptionRecordId != null ? exceptionRecordId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the exceptionRecordId fields are not set
        if (!(object instanceof ExceptionRecordEntity)) {
            return false;
        }
        ExceptionRecordEntity other = (ExceptionRecordEntity) object;
        if ((this.exceptionRecordId == null && other.exceptionRecordId != null) || (this.exceptionRecordId != null && !this.exceptionRecordId.equals(other.exceptionRecordId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ExceptionReccordEntity[ id=" + exceptionRecordId + " ]";
    }

}