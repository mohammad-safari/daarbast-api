package ce.web.daarbast.web.api;

import javax.security.auth.login.CredentialException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ce.web.daarbast.SecurityConstants;
import ce.web.daarbast.model.user.User;
import ce.web.daarbast.model.user.UserRepository;
import ce.web.daarbast.security.exceptions.MfaNeededForCompleteAuthenticationException;
import ce.web.daarbast.security.userdetails.DaarbastUserDetails;
import ce.web.daarbast.security.userdetails.DaarbastUserDetailsService;
import ce.web.daarbast.web.dto.LoginDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final DaarbastUserDetailsService daarbastUserDetailsService;
    private final UserRepository daarbastUserRepository;

    @PostMapping("")
    public User login(HttpServletResponse response, @RequestBody LoginDto loginDto)
            throws CredentialException, MfaNeededForCompleteAuthenticationException {
        var partialUserDetails = new DaarbastUserDetails();
        partialUserDetails.setUsername(loginDto.username());
        partialUserDetails.setPassword(loginDto.password());
        var auth = daarbastUserDetailsService.generateAuthorization(partialUserDetails, loginDto.mfaCode());
        var cookie = new Cookie(SecurityConstants.authorizationKey,
                SecurityConstants.tokenPrefix + auth);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
        return daarbastUserRepository.findByUsername(loginDto.username()).orElse(null);
    }

}
