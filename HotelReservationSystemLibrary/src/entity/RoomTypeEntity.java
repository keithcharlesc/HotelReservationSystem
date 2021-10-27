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

/**
 *
 * @author xianhui
 */
@Entity
public class RoomTypeEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomTypeId;
    @Column(nullable = false, length = 32)
    private String roomType;
    @Column(nullable = false)
    private Boolean isDisabled;
    @Column(nullable = false, length = 32)
    private String roomDescription;
    @Column(nullable = false)
    private Integer roomImportance; // 1- least important, if add additional room type need to make sure everything changes
    @Column(nullable = false)
    private Integer roomSize;
    @Column(nullable = false)
    private Integer roomBed;
    @Column(nullable = false)
    private Integer roomCapacity;
    @Column(nullable = false, length = 32)
    private String roomAmenities;

    @OneToMany(mappedBy = "roomType")
    private List<RoomRateEntity> roomRates;

    @OneToMany(mappedBy = "roomType")
    private List<RoomEntity> rooms;

    public RoomTypeEntity() {
        this.roomRates = new ArrayList<>();
        this.rooms = new ArrayList<>();
    }

    public RoomTypeEntity(String roomType, Boolean isDisabled, String roomDescription, int roomImportance, int roomSize, int roomBed, int roomCapacity, String roomAmenities) {
        this();
        this.roomType = roomType;
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

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
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
