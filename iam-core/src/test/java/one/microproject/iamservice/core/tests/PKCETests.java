package one.microproject.iamservice.core.tests;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.security.NoSuchAlgorithmException;
import java.util.stream.Stream;

import static one.microproject.iamservice.core.model.utils.TokenUtils.verifyPKCE;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PKCETests {

    private static Stream<Arguments> createPKCEArguments() {
        return Stream.of(
                Arguments.of("QGOXCQRDTFy6rD1nzdXRFqDAZEpqTonBFM_MT1b_zZY",  "S256", "w_yvspIAGrAMmLVcRXiXDtMrqNl4v-Ka1urbDdj7W9A", Boolean.TRUE),
                Arguments.of("Z7MF4wzDSW3PdS0OMPqpzAqBigxHGfgpxwRGdf-so80",  "S256", "g3LSJwhVJc7Up2J_CK1tCfdWmR2wwBEVd3yhUuck6CE", Boolean.TRUE),
                Arguments.of("xxxasdasdasdasdasdasdasdasdasdasdasdasdasda", "plain", "xxxasdasdasdasdasdasdasdasdasdasdasdasdasda", Boolean.TRUE),
                Arguments.of("xxxasdasdasdasdasdasdasdasdasdasdasdasdasda", "plain", "zzzasdasdasdasdasdasdasdasdasdasdasdasdasda", Boolean.FALSE),
                Arguments.of("Z7MF4wzDSW3PdS0OMPqpzAqBigxHGfgpxwRGdf-so80",  "S256", "w_yvspIAGrAMmLVcRXiXDtMrqNl4v-Ka1urbDdj7W9A", Boolean.FALSE)
        );
    }

    @ParameterizedTest
    @MethodSource("createPKCEArguments")
    public void testPKCE(String codeChallenge, String codeChallengeMethod, String codeVerifier, Boolean expectedResult) throws NoSuchAlgorithmException {
        boolean result = verifyPKCE(codeChallenge, codeChallengeMethod, codeVerifier);
        assertEquals(expectedResult, result);
    }

}
