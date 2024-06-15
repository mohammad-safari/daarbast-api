package ce.web.daarbast.security.exceptions;

public class MfaNeededForCompleteAuthenticationException extends Exception {
    public MfaNeededForCompleteAuthenticationException(String message) {
        super(message);
    }
}
