package itx.iamservice.core.model.tests;

import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.keys.Id;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IdTests {

    private static Stream<Arguments> provideTestEqualsData() {
        return Stream.of(
                Arguments.of(OrganizationId.from("org-01"), OrganizationId.from("org-01"), true),
                Arguments.of(OrganizationId.from("org-01"), OrganizationId.from("org-02"), false)
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestEqualsData")
    public void testEqualsAndHashCode(Id first, Id second, boolean expected) {
        assertEquals(first.equals(second), expected);
        assertEquals(first.hashCode() == second.hashCode(), expected);
    }

}
