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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;


@Entity
public class NightEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long nightId;
    
    @ManyToOne(fetch = FetchType.LAZY, optional=false)
    @JoinColumn(nullable = false)
    private RoomRateEntity roomRate;

    public NightEntity() {
    }

    public Long getNightId() {
        return nightId;
    }

    public void setNightId(Long nightId) {
        this.nightId = nightId;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the nightId fields are not set
        if (!(object instanceof NightEntity)) {
            return false;
        }
        NightEntity other = (NightEntity) object;
        if ((this.nightId == null && other.nightId != null) || (this.nightId != null && !this.nightId.equals(other.nightId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "NightEntity{" + "nightId=" + nightId + ", roomRate=" + roomRate + '}';
    }
    
}
