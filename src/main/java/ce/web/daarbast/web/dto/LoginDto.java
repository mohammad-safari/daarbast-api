package ce.web.daarbast.web.dto;

import org.springframework.lang.NonNull;

import jakarta.annotation.Nullable;

public record LoginDto(@NonNull String username, @NonNull String password, @Nullable String mfaCode) {

}
