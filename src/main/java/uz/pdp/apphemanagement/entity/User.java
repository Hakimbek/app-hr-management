package uz.pdp.apphemanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue
    private UUID id; // user's unique id

    @Column(nullable = false, length = 50)
    private String firstName; // user's firstName

    @Column(nullable = false, length = 50)
    private String lastName; // user's lastName

    @Column(nullable = false, unique = true)
    private String email; // user's unique email

    private String emailCode; // user's email code

    @Column(nullable = false)
    private String password; // user's password

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Timestamp createdAt; // user's creation time

    @UpdateTimestamp
    private Timestamp updatedAt; // user's update time

    @ManyToMany
    private Set<Role> roles; // user's roles

    private boolean accountNonExpired = true; // user's account has not expired

    private boolean accountNonLocked = true; // user's account has not locked

    private boolean credentialsNonExpired = true; // user's credentials has not expired

    private boolean enabled; // user's account is enabled

    // constructor
    public User(String firstName, String lastName, String email, String password, Set<Role> roles) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    //----------------METHODS OF USER DETAILS----------------//

    // return user roles
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
    }

    // return user unique email
    @Override
    public String getUsername() {
        return this.email;
    }

    // return boolean - user's account is expired or not
    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    // return boolean - user's account is locked or not
    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    // return boolean - user's credentials is expired or not
    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    // return boolean - user's account is enable or not
    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
