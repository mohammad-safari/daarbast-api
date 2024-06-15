package ce.web.daarbast.security.userdetails;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import ce.web.daarbast.model.user.User;
import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@Entity
@Table(name = "details")
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class DaarbastUserDetails implements UserDetails {
    @Id
    private String userId;

    @NonNull
    private String password;

    @NonNull
    @JoinColumn(name = "username")
    private String username;

    @Nullable
    private String mfaToken;

    private boolean mfaEnabled;

    @MapsId
    @JoinColumn(name = "user_id")
    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    // region unimplemented
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.stream(autorityString.split(",")).map(DaarbastAuthority::new).toList();

    }

    private String autorityString;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;
    // endregion
}

/**
 * not an important class just as a utility for creating authentication object
 */
@Data
@RequiredArgsConstructor
class DaarbastAuthority implements GrantedAuthority {
    private final String authority;
}