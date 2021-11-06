/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Entity;

@Entity
public class PeakRateEntity extends RoomRateEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public PeakRateEntity() {
        super();
    }

    public PeakRateEntity(String name, BigDecimal ratePerNight, LocalDateTime startDate, LocalDateTime endDate) {
        super(name, ratePerNight, startDate, endDate);
    }

    @Override
    public String toString() {
        return "PeakRateEntity{" + super.toString() + '}';
    }

}
