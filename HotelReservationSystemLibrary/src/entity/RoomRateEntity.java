package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;

//need to fix import for room type before can refract it, and create overloaded constructor 
@Entity
@Inheritance(strategy= InheritanceType.JOINED)
public abstract class RoomRateEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomRateId;
    @Column(nullable = false, precision = 11, scale = 2)
    @NotNull
    @Digits(integer = 9, fraction = 2)
    private BigDecimal ratePerNight;
    @Column(nullable = false)
    @NotNull
    private Date startDate;
    @Column(nullable = false)
    @Future
    @NotNull
    private Date endDate;
    @Column(nullable = false)
    @NotNull
    private Boolean isDisabled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private RoomTypeEntity roomType;

    @OneToMany(mappedBy = "roomRate")
    private List<NightEntity> nights;

    public RoomRateEntity() {
        this.nights = new ArrayList<>();
    }

    public RoomRateEntity(BigDecimal ratePerNight, Date startDate, Date endDate, Boolean isDisabled) {
        this();
        this.ratePerNight = ratePerNight;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isDisabled = isDisabled;
        this.roomType = roomType;
    }

    public Long getRoomRateId() {
        return roomRateId;
    }

    public void setRoomRateId(Long roomRateId) {
        this.roomRateId = roomRateId;
    }

    public BigDecimal getRatePerNight() {
        return ratePerNight;
    }

    public void setRatePerNight(BigDecimal ratePerNight) {
        this.ratePerNight = ratePerNight;
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

    public Boolean getIsDisabled() {
        return isDisabled;
    }

    public void setIsDisabled(Boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    public List<NightEntity> getNights() {
        return nights;
    }

    public void setNights(List<NightEntity> nights) {
        this.nights = nights;
    }

    public RoomTypeEntity getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomTypeEntity roomType) {
        this.roomType = roomType;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the roomRateId fields are not set
        if (!(object instanceof RoomRateEntity)) {
            return false;
        }
        RoomRateEntity other = (RoomRateEntity) object;
        if ((this.roomRateId == null && other.roomRateId != null) || (this.roomRateId != null && !this.roomRateId.equals(other.roomRateId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "roomRateId=" + roomRateId + ", ratePerNight=" + ratePerNight + ", startDate=" + startDate + ", endDate=" + endDate + ", isDisabled=" + isDisabled + ", roomType=" + roomType + ", nights=" + nights;
    }

    

}
