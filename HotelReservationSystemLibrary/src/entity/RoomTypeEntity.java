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
    
    @OneToMany(mappedBy="roomType")
    private List<RoomRateEntity> roomRates;
    
    @OneToMany(mappedBy="roomType")
    private List<Room> rooms;

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

    /**
     * @return the roomTyppe
     */
    public String getRoomTyppe() {
        return roomTyppe;
    }

    /**
     * @param roomTyppe the roomTyppe to set
     */
    public void setRoomTyppe(String roomTyppe) {
        this.roomTyppe = roomTyppe;
    }

    /**
     * @return the isDisabled
     */
    public Boolean getIsDisabled() {
        return isDisabled;
    }

    /**
     * @param isDisabled the isDisabled to set
     */
    public void setIsDisabled(Boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    /**
     * @return the roomDescription
     */
    public String getRoomDescription() {
        return roomDescription;
    }

    /**
     * @param roomDescription the roomDescription to set
     */
    public void setRoomDescription(String roomDescription) {
        this.roomDescription = roomDescription;
    }

    /**
     * @return the roomImportance
     */
    public int getRoomImportance() {
        return roomImportance;
    }

    /**
     * @param roomImportance the roomImportance to set
     */
    public void setRoomImportance(int roomImportance) {
        this.roomImportance = roomImportance;
    }

    /**
     * @return the roomSize
     */
    public int getRoomSize() {
        return roomSize;
    }

    /**
     * @param roomSize the roomSize to set
     */
    public void setRoomSize(int roomSize) {
        this.roomSize = roomSize;
    }

    /**
     * @return the roomBed
     */
    public int getRoomBed() {
        return roomBed;
    }

    /**
     * @param roomBed the roomBed to set
     */
    public void setRoomBed(int roomBed) {
        this.roomBed = roomBed;
    }

    /**
     * @return the roomCapacity
     */
    public int getRoomCapacity() {
        return roomCapacity;
    }

    /**
     * @param roomCapacity the roomCapacity to set
     */
    public void setRoomCapacity(int roomCapacity) {
        this.roomCapacity = roomCapacity;
    }

    /**
     * @return the roomAmenities
     */
    public String getRoomAmenities() {
        return roomAmenities;
    }

    /**
     * @param roomAmenities the roomAmenities to set
     */
    public void setRoomAmenities(String roomAmenities) {
        this.roomAmenities = roomAmenities;
    }

    /**
     * @return the roomrates
     */
    public List<RoomRate> getRoomrates() {
        return roomrates;
    }

    /**
     * @param roomrates the roomrates to set
     */
    public void setRoomrates(List<RoomRate> roomrates) {
        this.roomrates = roomrates;
    }

    /**
     * @return the rooms
     */
    public List<Room> getRooms() {
        return rooms;
    }

    /**
     * @param rooms the rooms to set
     */
    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }
    
}
