/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import util.enumeration.EmployeeAccessRightEnum;

@Entity
public class EmployeeEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;
    @Column(nullable = false, length = 32)
    private String firstName;
    @Column(nullable = false, length = 32)
    private String lastName;
    @Column(nullable = false, length = 32)
    private String password;
    @Column(nullable = false, unique = true, length = 32)
    private String username;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmployeeAccessRightEnum employeeAccessRightEnum;

    public EmployeeEntity() {
    }

    public EmployeeEntity(String firstName, String lastName, String username, String password, EmployeeAccessRightEnum employeeAccessRightEnum) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.employeeAccessRightEnum = employeeAccessRightEnum;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }
    
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public EmployeeAccessRightEnum getEmployeeAccessRightEnum() {
        return employeeAccessRightEnum;
    }

    public void setEmployeeAccessRightEnum(EmployeeAccessRightEnum employeeAccessRightEnum) {
        this.employeeAccessRightEnum = employeeAccessRightEnum;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the employeeId fields are not set
        if (!(object instanceof EmployeeEntity)) {
            return false;
        }
        EmployeeEntity other = (EmployeeEntity) object;
        if ((this.employeeId == null && other.employeeId != null) || (this.employeeId != null && !this.employeeId.equals(other.employeeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "EmployeeEntity{" + "employeeId=" + employeeId + ", firstName=" + firstName + ", lastName=" + lastName + ", password=" + password + ", username=" + username + ", employeeAccessRightEnum=" + employeeAccessRightEnum + '}';
    }


}
