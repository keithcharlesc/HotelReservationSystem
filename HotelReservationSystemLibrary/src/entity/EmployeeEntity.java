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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import util.enumeration.EmployeeAccessRightEnum;

@Entity
public class EmployeeEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(min = 6, max = 32)
    private String password;
    @Column(nullable = false, unique = true, length = 32)
    @NotNull
    @Size(min = 4, max = 32)
    private String username;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private EmployeeAccessRightEnum employeeAccessRightEnum;

    public EmployeeEntity() {
    }

    public EmployeeEntity(String username, String password, EmployeeAccessRightEnum employeeAccessRightEnum) {
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
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public EmployeeAccessRightEnum getEmployeeAccessRightEnum() {
        return employeeAccessRightEnum;
    }

    public void setEmployeeAccessRightEnum(EmployeeAccessRightEnum employeeAccessRightEnum) {
        this.employeeAccessRightEnum = employeeAccessRightEnum;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the employeeId fields are not set
        if (!(object instanceof EmployeeEntity)) {
            return false;
        }
        EmployeeEntity other = (EmployeeEntity) object;
        if ((this.getEmployeeId() == null && other.getEmployeeId() != null) || (this.getEmployeeId() != null && !this.employeeId.equals(other.employeeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "EmployeeEntity{" + "employeeId=" + employeeId + ", password=" + password + ", username=" + username + ", employeeAccessRightEnum=" + employeeAccessRightEnum + '}';
    }

}
