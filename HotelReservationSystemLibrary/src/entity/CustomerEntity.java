package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class CustomerEntity extends GuestEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(nullable = false, length = 32)
    @NotNull
    @Size(min = 6, max = 32)
    private String password;

    public CustomerEntity() {
        super();
    }

    public CustomerEntity(String name, String email, String phoneNumber, String password) {
        super(name, email, phoneNumber);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the customerId fields are not set
        if (!(object instanceof CustomerEntity)) {
            return false;
        }
        CustomerEntity other = (CustomerEntity) object;
        if ((this.getGuestId() == null && other.getGuestId() != null) || (this.getGuestId() != null && !this.getGuestId().equals(other.getGuestId()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + "CustomerEntity{" + "password=" + password + '}';
    }

}
