package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class RoomRateEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long roomRateId;
    @Column(nullable = false, unique = true)
    @NotNull
    protected String name;
    @Column(nullable = false, precision = 11, scale = 2)
    @NotNull
    @Digits(integer = 9, fraction = 2)
    protected BigDecimal ratePerNight;

    @Column(nullable = false)
    @NotNull
    protected boolean isDisabled;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private RoomTypeEntity roomType;

    @OneToMany(mappedBy = "roomRate")
    private List<NightEntity> nights;

    public RoomRateEntity() {
        this.nights = new ArrayList<>();
    }

    public RoomRateEntity(String name, BigDecimal ratePerNight) {
        this();
        this.name = name;
        this.ratePerNight = ratePerNight;
        this.isDisabled = false;
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

//    public LocalDateTime getStartDate() {
//        return startDate;
//    }
//
//    public void setStartDate(LocalDateTime startDate) {
//        this.startDate = startDate;
//    }
//
//    public LocalDateTime getEndDate() {
//        return endDate;
//    }
//
//    public void setEndDate(LocalDateTime endDate) {
//        this.endDate = endDate;
//    }
    public boolean getIsDisabled() {
        return isDisabled;
    }

    public void setIsDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    public RoomTypeEntity getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomTypeEntity roomType) {
        this.roomType = roomType;
    }

    public List<NightEntity> getNights() {
        return nights;
    }

    public void setNights(List<NightEntity> nights) {
        this.nights = nights;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the roomRateId fields are not set
        if (!(object instanceof RoomRateEntity)) {
            return false;
        }
        RoomRateEntity other = (RoomRateEntity) object;
        if ((this.getRoomRateId() == null && other.getRoomRateId() != null) || (this.getRoomRateId() != null && !this.roomRateId.equals(other.roomRateId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "RoomRateEntity{" + "roomRateId=" + roomRateId + ", name=" + name + ", ratePerNight=" + ratePerNight + ", isDisabled=" + isDisabled + ", roomType=" + roomType + ", nights=" + nights + '}';
    }

}
