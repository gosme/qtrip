package qtriptest.API_tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.response.Response;

public class testCase_API_01 extends APITestBase {
    @Test(groups = { "API Tests" }, priority = 1,
            description = "Verify that a new user can be registered and login using APIs of QTrip")
    public void testCase_API_01() {
        String email = buildUniqueEmail("qtrip.api.register");
        String password = "abc@123";

        // Step 1: Use the register API to register a new user.
        Response registerResponse = registerUser(email, password);
        Assert.assertEquals(registerResponse.getStatusCode(), 201, "Register API did not return status code 201");

        // Step 2: Use the Login API to login using the registered user.
        Response loginResponse = loginUser(email, password);

        // Step 3 and 4: Validate that login succeeds and token/user id are returned.
        Assert.assertEquals(loginResponse.getStatusCode(), 201, "Login API did not return status code 201");
        Assert.assertTrue(extractSuccess(loginResponse), "Login response did not contain success=true");
        Assert.assertNotNull(extractToken(loginResponse), "Token was not returned by login API");
        Assert.assertNotNull(extractUserId(loginResponse), "User id was not returned by login API");
    }
}
