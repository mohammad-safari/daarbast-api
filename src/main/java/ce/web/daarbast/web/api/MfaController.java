package ce.web.daarbast.web.api;

import java.util.List;

import javax.security.auth.login.CredentialException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ce.web.daarbast.security.userdetails.DaarbastUserDetails;
import ce.web.daarbast.security.userdetails.DaarbastUserDetailsService;
import ce.web.daarbast.web.dto.LoginDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth/mfa")
@RequiredArgsConstructor
public class MfaController {
    private final DaarbastUserDetailsService daarbastUserDetailsService;

    @PostMapping("")
    public List<String> singup(@RequestParam(required = false, defaultValue = "true") boolean activate, @RequestBody LoginDto loginDto)
            throws CredentialException {
        var partialUserDetails = new DaarbastUserDetails();
        partialUserDetails.setUsername(loginDto.username());
        partialUserDetails.setPassword(loginDto.password());
        if (activate) {
            daarbastUserDetailsService.enableUserMfa(partialUserDetails);
            daarbastUserDetailsService.getRecoveryCodes(partialUserDetails);
        }
        daarbastUserDetailsService.disableUserMfa(partialUserDetails);
        return null;

    }

    @PostMapping("recovery-codes")
    public List<String> recoveryCodes(@RequestBody LoginDto loginDto) throws CredentialException {
        var partialUserDetails = new DaarbastUserDetails();
        partialUserDetails.setUsername(loginDto.username());
        partialUserDetails.setPassword(loginDto.password());
        return daarbastUserDetailsService.getRecoveryCodes(partialUserDetails);
    }
}
