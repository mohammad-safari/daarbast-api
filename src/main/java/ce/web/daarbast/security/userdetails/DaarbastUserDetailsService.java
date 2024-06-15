package ce.web.daarbast.security.userdetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.security.auth.login.CredentialException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ce.web.daarbast.security.JwtService;
import ce.web.daarbast.security.MfaService;
import ce.web.daarbast.security.exceptions.MfaNeededForCompleteAuthenticationException;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityExistsException;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DaarbastUserDetailsService implements UserDetailsService {
    private final UserDetailsRepository userDetailsRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final MfaService mfaService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDetailsRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User name not found: " + username));
    }

    public void registerUser(DaarbastUserDetails userDetails) throws EntityExistsException {
        var encodedPasasword = passwordEncoder.encode(userDetails.getPassword());
        userDetails.setPassword(encodedPasasword);
        userDetailsRepository.save(userDetails);
    }

    public void authenticateUser(DaarbastUserDetails userDetails) throws CredentialException {
        var expectedUsername = userDetails.getUsername();
        var expectedEncodedPassword = loadUserByUsername(expectedUsername).getPassword();
        if (passwordEncoder.matches(userDetails.getPassword(), expectedEncodedPassword)) {
            return;
        }
        throw new CredentialException("Credential is not valid: " + expectedUsername);
    }

    public String generateAuthorization(DaarbastUserDetails userDetails, @Nullable String code)
            throws CredentialException, MfaNeededForCompleteAuthenticationException {
        authenticateUser(userDetails);
        var expectedUsername = userDetails.getUsername();
        var retrievedUserDetails = userDetailsRepository.findByUsername(expectedUsername).orElse(null);
        if (retrievedUserDetails == null) {
            return null;
        }
        verifyMfaCodeIfEnabled(userDetails, code, expectedUsername, retrievedUserDetails);
        var authentication = new DaarbastAuthentication(userDetails.getUsername(), Set.<GrantedAuthority>of());
        return jwtService.generateToken(authentication);
    }

    public void enableUserMfa(DaarbastUserDetails userDetails) throws CredentialException {
        authenticateUser(userDetails);
        var retrievedUserDetails = userDetailsRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (retrievedUserDetails == null) {
            return;
        }
        retrievedUserDetails.setMfaEnabled(true);
        retrievedUserDetails.setMfaToken(mfaService.generateSecretKey());
        userDetailsRepository.save(userDetails);
    }

    public void disableUserMfa(DaarbastUserDetails userDetails) throws CredentialException {
        authenticateUser(userDetails);
        var retrievedUserDetails = userDetailsRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (retrievedUserDetails == null) {
            return;
        }
        retrievedUserDetails.setMfaEnabled(false);
        retrievedUserDetails.setMfaToken(null);
        userDetailsRepository.save(userDetails);
    }

    public List<String> getRecoveryCodes(DaarbastUserDetails userDetails) throws CredentialException {
        authenticateUser(userDetails);
        return mfaService.generateRecoveryCodes();
    }

    private void verifyMfaCodeIfEnabled(DaarbastUserDetails userDetails, String code, String expectedUsername,
            DaarbastUserDetails retrievedUserDetails)
            throws MfaNeededForCompleteAuthenticationException, CredentialException {
        if (retrievedUserDetails.isMfaEnabled()) {
            if (StringUtils.isEmpty(code)) {
                throw new MfaNeededForCompleteAuthenticationException(
                        "Another authentication factor is needed for login:" + expectedUsername);
            }
            if (mfaService.verifyTotp(userDetails.getMfaToken(), code)) {
                throw new CredentialException("Mfa code is not valid: " + expectedUsername);
            }
        }
    }
}

/**
 * not an important class just as a utility for creating authentication object
 */
@Data
@RequiredArgsConstructor
class DaarbastAuthentication implements Authentication {
    private final String name;
    private final Collection<GrantedAuthority> authorities;
    private DaarbastUserDetails details;
    private boolean authenticated;
    private Object credentials;
    private Object principal;
}