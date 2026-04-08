package qtriptest.API_tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.response.Response;

public class testCase_API_04 extends APITestBase {
    @Test(groups = { "API Tests" }, priority = 4,
            description = "Verify that a duplicate user account cannot be created on the QTrip Website")
    public void testCase_API_04() {
        String email = buildUniqueEmail("qtrip.api.duplicate");
        String password = "abc@123";

        // Step 1: Register a new user.
        Response firstRegistrationResponse = registerUser(email, password);
        Assert.assertEquals(firstRegistrationResponse.getStatusCode(), 201,
                "First registration did not return status code 201");

        // Step 2: Again register a new user with the same email id and validate duplicate handling.
        Response duplicateRegistrationResponse = registerUser(email, password);
        Assert.assertEquals(duplicateRegistrationResponse.getStatusCode(), 400,
                "Duplicate registration did not return status code 400");
        Assert.assertTrue(duplicateRegistrationResponse.getBody().asString().contains("Email already exists"),
                "Duplicate registration response did not contain the expected message");
    }
}
