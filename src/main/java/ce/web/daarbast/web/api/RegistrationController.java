package ce.web.daarbast.web.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ce.web.daarbast.model.user.User;
import ce.web.daarbast.model.user.UserRepository;
import ce.web.daarbast.security.userdetails.DaarbastUserDetails;
import ce.web.daarbast.security.userdetails.DaarbastUserDetailsService;
import ce.web.daarbast.web.dto.SignupDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class RegistrationController {
    private final DaarbastUserDetailsService daarbastUserDetailsService;
    private final UserRepository daarbastUserRepository;

    @PostMapping("register")
    public User singup(@RequestBody SignupDto signupDto) {
        var userModel = new User();
        userModel.setUsername(signupDto.username());
        userModel.setEmail(signupDto.email());
        daarbastUserRepository.save(userModel);
        var partialUserDetails = new DaarbastUserDetails();
        partialUserDetails.setUser(userModel);
        partialUserDetails.setUsername(signupDto.username());
        partialUserDetails.setPassword(signupDto.password());
        daarbastUserDetailsService.registerUser(partialUserDetails);
        return daarbastUserRepository.findByUsername(signupDto.username()).orElse(null);
    }

}
