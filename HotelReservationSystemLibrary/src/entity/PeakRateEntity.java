/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author xianhui
 */
@Entity
public class PeakRateEntity extends RoomRateEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public PeakRateEntity() {
        super();
    }

    public PeakRateEntity(BigDecimal ratePerNight, Date startDate, Date endDate, Boolean isDisabled) {
        super(ratePerNight, startDate, endDate, isDisabled);
    }

    @Override
    public String toString() {
        return "PeakRateEntity{" + super.toString() + '}';
    }
    
}
