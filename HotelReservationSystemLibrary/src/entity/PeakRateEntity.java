/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;

@Entity
public class PeakRateEntity extends RoomRateEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(nullable = false)
    @NotNull
    private Date startDate;
    @Column(nullable = false)
    @Future
    @NotNull
    private Date endDate;

    public PeakRateEntity() {
        super();
    }

    public PeakRateEntity(String name, BigDecimal ratePerNight) {
        this();
        this.name = name;
        this.ratePerNight = ratePerNight;
    }

    public PeakRateEntity(String name, BigDecimal ratePerNight, Date startDate, Date endDate) {
        this(name, ratePerNight);
        this.startDate = startDate;
        this.endDate = endDate;
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

    @Override
    public String toString() {
        return "PeakRateEntity{" + super.toString() + ", startDate : " + getStartDate() + ", endDate : " + getEndDate() + '}';
    }

}
