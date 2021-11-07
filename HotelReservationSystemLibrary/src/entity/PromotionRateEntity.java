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
public class PromotionRateEntity extends RoomRateEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(nullable = false)
    @NotNull
    private LocalDateTime startDate;
    @Column(nullable = false)
    @Future
    @NotNull
    private LocalDateTime endDate;

    public PromotionRateEntity() {
        super();
    }

    public PromotionRateEntity(String name, BigDecimal ratePerNight) {
        this();
        this.name = name;
        this.ratePerNight = ratePerNight;
    }

    public PromotionRateEntity(String name, BigDecimal ratePerNight, LocalDateTime startDate, LocalDateTime endDate) {
        this(name, ratePerNight);
        this.startDate = startDate;
        this.endDate = endDate;
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

    @Override
    public String toString() {
        return "PromotionRateEntity{" + super.toString() + ", startDate : " + getStartDate() + ", endDate : " + getEndDate() + '}';
    }

}
