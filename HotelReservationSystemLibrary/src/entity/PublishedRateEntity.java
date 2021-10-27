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
 * @author keithcharleschan
 */
@Entity
public class PublishedRateEntity extends RoomRateEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public PublishedRateEntity() {
        super();
    }

    public PublishedRateEntity(BigDecimal ratePerNight, Date startDate, Date endDate, Boolean isDisabled) {
        super(ratePerNight, startDate, endDate, isDisabled);
    }

    
//    @Override
//    public int hashCode() {
//        int hash = 0;
//        hash += (id != null ? id.hashCode() : 0);
//        return hash;
//    }
//
//    @Override
//    public boolean equals(Object object) {
//        // TODO: Warning - this method won't work in the case the id fields are not set
//        if (!(object instanceof PublishedRateEntity)) {
//            return false;
//        }
//        PublishedRateEntity other = (PublishedRateEntity) object;
//        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    public String toString() {
//        return "entity.PublishedRateEntity[ id=" + id + " ]";
//    }
    
}
