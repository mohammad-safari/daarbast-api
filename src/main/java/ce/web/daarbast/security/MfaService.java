package ce.web.daarbast.security;

import java.util.List;

import org.springframework.stereotype.Service;

import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrDataFactory;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.recovery.RecoveryCodeGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.util.Utils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MfaService {
    private final RecoveryCodeGenerator recoveryCodeGenerator;
    private final SecretGenerator secretGenerator;
    private final QrGenerator qrGenerator;
    private final CodeVerifier codeVerifier;
    private final QrDataFactory qrDataFactory;

    public String generateSecretKey() {
        return secretGenerator.generate();
    }

    public String getQRCode(String secret) throws QrGenerationException {
        QrData data = qrDataFactory.newBuilder()
                .label("MFA")
                .secret(secret)
                .issuer("Daarbast")
                .build();
        return Utils.getDataUriForImage(
                qrGenerator.generate(data),
                qrGenerator.getImageMimeType());
    }

    public List<String> generateRecoveryCodes() {
        return List.of(recoveryCodeGenerator.generateCodes(16));
    }

    public boolean verifyTotp(String code, String secret) {
        return codeVerifier.isValidCode(secret, code);
    }
}
