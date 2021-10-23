/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author xianhui
 */
@Entity
public class RoomTypeEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long roomTypeId;
    private String roomTyppe;
    private Boolean isDisabled;
    private String roomDescription;
    private int roomImportance; // 1- least important, if add additional room type need to make sure everything changes
    private int roomSize;
    private int roomBed;
    private int roomCapacity;
    private String roomAmenities;

    @OneToMany(mappedBy = "roomType")
    private List<RoomRateEntity> roomRates;

    @OneToMany(mappedBy = "roomType")
    private List<RoomEntity> rooms;

    public RoomTypeEntity() {
        this.roomRates = new ArrayList<>();
        this.rooms = new ArrayList<>();
    }

    public RoomTypeEntity(String roomTyppe, Boolean isDisabled, String roomDescription, int roomImportance, int roomSize, int roomBed, int roomCapacity, String roomAmenities) {
        this();
        this.roomTyppe = roomTyppe;
        this.isDisabled = isDisabled;
        this.roomDescription = roomDescription;
        this.roomImportance = roomImportance;
        this.roomSize = roomSize;
        this.roomBed = roomBed;
        this.roomCapacity = roomCapacity;
        this.roomAmenities = roomAmenities;
    }

    public Long getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(Long roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public String getRoomTyppe() {
        return roomTyppe;
    }

    public void setRoomTyppe(String roomTyppe) {
        this.roomTyppe = roomTyppe;
    }

    public Boolean getIsDisabled() {
        return isDisabled;
    }

    public void setIsDisabled(Boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    public String getRoomDescription() {
        return roomDescription;
    }

    public void setRoomDescription(String roomDescription) {
        this.roomDescription = roomDescription;
    }

    public int getRoomImportance() {
        return roomImportance;
    }

    public void setRoomImportance(int roomImportance) {
        this.roomImportance = roomImportance;
    }

    public int getRoomSize() {
        return roomSize;
    }

    public void setRoomSize(int roomSize) {
        this.roomSize = roomSize;
    }

    public int getRoomBed() {
        return roomBed;
    }

    public void setRoomBed(int roomBed) {
        this.roomBed = roomBed;
    }

    public int getRoomCapacity() {
        return roomCapacity;
    }

    public void setRoomCapacity(int roomCapacity) {
        this.roomCapacity = roomCapacity;
    }

    public String getRoomAmenities() {
        return roomAmenities;
    }

    public void setRoomAmenities(String roomAmenities) {
        this.roomAmenities = roomAmenities;
    }

    public List<RoomRateEntity> getRoomrates() {
        return roomRates;
    }

    public void setRoomrates(List<RoomRateEntity> roomrates) {
        this.roomRates = roomrates;
    }

    public List<RoomEntity> getRooms() {
        return rooms;
    }

    public void setRooms(List<RoomEntity> rooms) {
        this.rooms = rooms;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomTypeId != null ? roomTypeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the roomTypeId fields are not set
        if (!(object instanceof RoomTypeEntity)) {
            return false;
        }
        RoomTypeEntity other = (RoomTypeEntity) object;
        if ((this.roomTypeId == null && other.roomTypeId != null) || (this.roomTypeId != null && !this.roomTypeId.equals(other.roomTypeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RoomTypeEntity[ id=" + roomTypeId + " ]";
    }

}
