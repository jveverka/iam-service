package one.microproject.iamservice.core.model.tests;


import one.microproject.iamservice.core.model.Permission;
import one.microproject.iamservice.core.model.PermissionParsingException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


class PermissionParsingTests {

    private static Stream<Arguments> providePropertiesData() {
        return Stream.of(
                Arguments.of(null, false),
                Arguments.of("", false),
                Arguments.of("service", false),
                Arguments.of("service.resource", false),
                Arguments.of("service.resource.action", true)
        );
    }

    @ParameterizedTest
    @MethodSource("providePropertiesData")
    void testPersistenceStringTransformations(String input, boolean expected) {
        try {
            Permission permission = Permission.from(input);
            assertTrue(expected);
            assertNotNull(permission);
            assertNotNull(permission.asStringValue());
            assertNotNull(permission.getService());
            assertNotNull(permission.getResource());
            assertNotNull(permission.getAction());
            assertNotNull(permission.getId());
            assertNotNull(permission.getId().getId());
        } catch (PermissionParsingException e) {
            assertFalse(expected);
        }
    }

}
