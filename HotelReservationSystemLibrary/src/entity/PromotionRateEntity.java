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
import javax.persistence.Entity;

@Entity
public class PromotionRateEntity extends RoomRateEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public PromotionRateEntity() {
        super();
    }

    public PromotionRateEntity(BigDecimal ratePerNight, LocalDateTime startDate, LocalDateTime endDate, Boolean isDisabled) {
        super(ratePerNight, startDate, endDate, isDisabled);
    }

    @Override
    public String toString() {
        return "PromotionRateEntity{" + super.toString() + '}';
    }

}
