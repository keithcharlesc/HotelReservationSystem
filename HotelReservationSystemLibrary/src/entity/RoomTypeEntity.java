/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class RoomTypeEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomTypeId;
    @Column(nullable = false, unique = true, length = 32)
    @NotNull
    @Size(min = 2, max = 32)
    private String roomTypeName;
    @Column(nullable = false)
    @NotNull
    private boolean isDisabled;
    @Column(nullable = false, length = 250)
    @NotNull
    @Size(min = 2, max = 250)
    private String roomDescription;

    @Column(nullable = false)
    @NotNull
    @Min(1)
    private Integer roomSize;
    @Column(nullable = false)
    @NotNull
    @Min(1)
    private Integer roomBed;
    @Column(nullable = false)
    @NotNull
    @Min(1)
    private Integer roomCapacity;
    @Column(nullable = false, length = 128)
    @NotNull
    @Size(min = 2, max = 128)
    private String roomAmenities;

    @Column(nullable = false, length = 128)
    @NotNull
    @Size(min = 1, max = 128)
    private String nextRoomType;

    @OneToMany(mappedBy = "roomType")
    private List<RoomRateEntity> roomRates;

    @OneToMany(mappedBy = "roomType")
    private List<RoomEntity> rooms;

    public RoomTypeEntity() {
        this.roomRates = new ArrayList<>();
        this.rooms = new ArrayList<>();
        this.isDisabled = false;
        this.roomDescription = "Big";
        this.roomSize = 1;
        this.roomBed = 2;
        this.roomCapacity = 2;
        this.roomAmenities = "With Pool";
    }

    public RoomTypeEntity(String roomTypeName, String nextRoomType) {
        this();
        this.roomTypeName = roomTypeName;
        this.nextRoomType = nextRoomType;
    }

    public Long getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(Long roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public String getRoomTypeName() {
        return roomTypeName;
    }

    public void setRoomTypeName(String roomTypeName) {
        this.roomTypeName = roomTypeName;
    }

    public boolean getIsDisabled() {
        return isDisabled;
    }

    public void setIsDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    public String getRoomDescription() {
        return roomDescription;
    }

    public void setRoomDescription(String roomDescription) {
        this.roomDescription = roomDescription;
    }

    public Integer getRoomSize() {
        return roomSize;
    }

    public void setRoomSize(Integer roomSize) {
        this.roomSize = roomSize;
    }

    public Integer getRoomBed() {
        return roomBed;
    }

    public void setRoomBed(Integer roomBed) {
        this.roomBed = roomBed;
    }

    public Integer getRoomCapacity() {
        return roomCapacity;
    }

    public void setRoomCapacity(Integer roomCapacity) {
        this.roomCapacity = roomCapacity;
    }

    public String getRoomAmenities() {
        return roomAmenities;
    }

    public void setRoomAmenities(String roomAmenities) {
        this.roomAmenities = roomAmenities;
    }

    public List<RoomRateEntity> getRoomRates() {
        return roomRates;
    }

    public void setRoomRates(List<RoomRateEntity> roomRates) {
        this.roomRates = roomRates;
    }

    public List<RoomEntity> getRooms() {
        return rooms;
    }

    public void setRooms(List<RoomEntity> rooms) {
        this.rooms = rooms;
    }

    public String getNextRoomType() {
        return nextRoomType;
    }

    public void setNextRoomType(String nextRoomType) {
        this.nextRoomType = nextRoomType;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the roomTypeId fields are not set
        if (!(object instanceof RoomTypeEntity)) {
            return false;
        }
        RoomTypeEntity other = (RoomTypeEntity) object;
        if ((this.getRoomTypeId() == null && other.getRoomTypeId() != null) || (this.getRoomTypeId() != null && !this.roomTypeId.equals(other.roomTypeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "RoomTypeEntity{" + "roomTypeId=" + roomTypeId + ", roomTypeName=" + roomTypeName + ", isDisabled=" + isDisabled + ", nextRoomType=" + nextRoomType + ", roomRates=" + roomRates + ", rooms=" + rooms + '}';
    }

   

}
