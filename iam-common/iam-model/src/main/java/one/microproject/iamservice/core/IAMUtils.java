package one.microproject.iamservice.core;

import one.microproject.iamservice.core.model.PKCEMethod;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

public final class IAMUtils {

    private IAMUtils() {
    }

    /**
     * Generate code_verifier as specified in https://tools.ietf.org/html/rfc7636#section-4.1
     * @return code_verifier = high-entropy cryptographic random STRING
     */
    public static String generateCodeVerifier() {
        return UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();
    }

    /**
     * https://tools.ietf.org/html/rfc7636#section-4.1
     * @param codeVerifier - code_verifier
     * @param codeChallengeMethod - code_challenge_method
     * @return code_challenge
     * @throws NoSuchAlgorithmException
     */
    public static String generateCodeChallenge(String codeVerifier, PKCEMethod codeChallengeMethod) throws NoSuchAlgorithmException {
        if (PKCEMethod.PLAIN.equals(codeChallengeMethod)) {
            return codeVerifier;
        } else if (PKCEMethod.S256.equals(codeChallengeMethod)) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(codeVerifier.getBytes(StandardCharsets.UTF_8));
            byte[] encodedBase64 = Base64.getUrlEncoder().withoutPadding().encode(encodedHash);
            return new String(encodedBase64, StandardCharsets.UTF_8);
        } else {
            throw new UnsupportedOperationException("PKCE code challenge method " + codeChallengeMethod + " is not supported.");
        }
    }

    /**
     * PKCE verification as specified in https://tools.ietf.org/html/rfc7636
     * @param codeChallenge - code_challenge
     * @param codeChallengeMethod - code_challenge_method
     * @param codeVerifier - code_verifier
     * @return true if code_challenge is verified successfully according RFC7636.
     */
    public static boolean verifyPKCE(String codeChallenge, PKCEMethod codeChallengeMethod, String codeVerifier) {
        if (codeVerifier == null) {
            return false;
        }
        if (codeChallenge.length() < 43) {
            return false;
        }
        if (PKCEMethod.PLAIN.equals(codeChallengeMethod)) {
            return codeChallenge.equals(codeVerifier);
        }
        if (PKCEMethod.S256.equals(codeChallengeMethod)) {
            try {
                String calculatedVerifier = generateCodeChallenge(codeVerifier, codeChallengeMethod);
                return codeChallenge.equals(calculatedVerifier);
            } catch (NoSuchAlgorithmException e) {
                return false;
            }
        }
        return false;
    }

}
