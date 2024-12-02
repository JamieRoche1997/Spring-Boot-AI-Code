package ie.spring.report.aicode.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "app_user") // Avoid using reserved keyword 'user'
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppUser implements UserDetails {

    @Id
    @NotBlank(message = "Username is mandatory")
    @Email(message = "Username must be a valid email")
    private String username;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Role is mandatory")
    private String role;

    private boolean unlocked = true;

    // Implementing UserDetails interface methods

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Modify if you handle account expiration
    }

    @Override
    public boolean isAccountNonLocked() {
        return unlocked; // Reflects the 'unlocked' field
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Modify if you handle credentials expiration
    }

    @Override
    public boolean isEnabled() {
        return true; // Modify if you handle user enabling/disabling
    }
}

