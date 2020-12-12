package one.microproject.iamservice.core.model.tests;

import one.microproject.iamservice.core.model.PKCEMethod;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.security.NoSuchAlgorithmException;
import java.util.stream.Stream;

import static one.microproject.iamservice.core.IAMUtils.generateCodeChallenge;
import static one.microproject.iamservice.core.IAMUtils.generateCodeVerifier;
import static one.microproject.iamservice.core.IAMUtils.verifyPKCE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PKCETests {

    private static Stream<Arguments> createPKCEVerifierArguments() {
        return Stream.of(
                Arguments.of("QGOXCQRDTFy6rD1nzdXRFqDAZEpqTonBFM_MT1b_zZY", PKCEMethod.S256,  "w_yvspIAGrAMmLVcRXiXDtMrqNl4v-Ka1urbDdj7W9A", Boolean.TRUE),
                Arguments.of("Z7MF4wzDSW3PdS0OMPqpzAqBigxHGfgpxwRGdf-so80", PKCEMethod.S256,  "g3LSJwhVJc7Up2J_CK1tCfdWmR2wwBEVd3yhUuck6CE", Boolean.TRUE),
                Arguments.of("xxxasdasdasdasdasdasdasdasdasdasdasdasdasda", PKCEMethod.PLAIN, "xxxasdasdasdasdasdasdasdasdasdasdasdasdasda", Boolean.TRUE),
                Arguments.of("xxxasdasdasdasdasdasdasdasdasdasdasdasdasda", PKCEMethod.PLAIN, "zzzasdasdasdasdasdasdasdasdasdasdasdasdasda", Boolean.FALSE),
                Arguments.of("Z7MF4wzDSW3PdS0OMPqpzAqBigxHGfgpxwRGdf-so80", PKCEMethod.S256,  "w_yvspIAGrAMmLVcRXiXDtMrqNl4v-Ka1urbDdj7W9A", Boolean.FALSE)
        );
    }

    @ParameterizedTest
    @MethodSource("createPKCEVerifierArguments")
    public void testPKCEVerifier(String codeChallenge, PKCEMethod codeChallengeMethod, String codeVerifier, Boolean expectedResult) throws NoSuchAlgorithmException {
        boolean result = verifyPKCE(codeChallenge, codeChallengeMethod, codeVerifier);
        assertEquals(expectedResult, result);
    }

    private static Stream<Arguments> createPKCEGeneratorAndVerifierArguments() {
        return Stream.of(
                Arguments.of(PKCEMethod.S256),
                Arguments.of(PKCEMethod.PLAIN)
        );
    }

    @ParameterizedTest
    @MethodSource("createPKCEGeneratorAndVerifierArguments")
    public void testPKCEGeneratorAndVerifier(PKCEMethod codeChallengeMethod) throws NoSuchAlgorithmException {
        String codeVerifier = generateCodeVerifier();
        String codeChallenge = generateCodeChallenge(codeVerifier, codeChallengeMethod);
        boolean result = verifyPKCE(codeChallenge, codeChallengeMethod, codeVerifier);
        assertTrue(result);
        result = verifyPKCE(codeChallenge, codeChallengeMethod, generateCodeVerifier());
        assertFalse(result);
    }

}
