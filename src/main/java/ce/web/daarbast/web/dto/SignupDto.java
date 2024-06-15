package ce.web.daarbast.web.dto;

import org.springframework.lang.NonNull;

public record SignupDto(@NonNull String username, @NonNull String email, @NonNull String password) {

}
