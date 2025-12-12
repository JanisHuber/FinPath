package ciCdTests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class CiCdTest {

    @Test
    void shouldPassInGithubActions() {
        assertTrue(true, "This test should always pass");
    }

    /*
    @Test
    void shouldFailInGithubActions() {
        fail("This test should always fail");
    }
    */
}
