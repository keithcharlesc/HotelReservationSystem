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
import javax.persistence.OneToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

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
    @Column(nullable = false)
    @NotNull
    @Min(1)
    @Max(2)
    private Integer typeOfException;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private ReservationRoomEntity reservationRoom;

    public ExceptionRecordEntity() {
    }

    public ExceptionRecordEntity(Integer typeOfException) {
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

    public void setTypeOfException(Integer typeOfException) {
        this.typeOfException = typeOfException;
    }

    public ReservationRoomEntity getReservationRoom() {
        return reservationRoom;
    }

    public void setReservationRoom(ReservationRoomEntity reservationRoom) {
        this.reservationRoom = reservationRoom;
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
        return "ExceptionRecordEntity{" + "exceptionRecordId=" + exceptionRecordId + ", typeOfException=" + typeOfException + ", reservationRoom=" + reservationRoom + '}';
    }

}
