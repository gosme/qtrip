package qtriptest.API_tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.response.Response;

public class testCase_API_03 extends APITestBase {
    @Test(groups = { "API Tests" }, priority = 3,
            description = "Verify that a reservation can be made using the QTrip API")
    public void testCase_API_03() {
        String email = buildUniqueEmail("qtrip.api.booking");
        String password = "abc@123";
        String guestName = "API Guest";
        String travelDate = "2027-01-01";
        String personCount = "2";

        // Step 1: Create a new user using API and login.
        Response registerResponse = registerUser(email, password);
        Assert.assertEquals(registerResponse.getStatusCode(), 201, "Register API did not return status code 201");

        Response loginResponse = loginUser(email, password);
        Assert.assertEquals(loginResponse.getStatusCode(), 201, "Login API did not return status code 201");

        String token = extractToken(loginResponse);
        String userId = extractUserId(loginResponse);
        Assert.assertNotNull(token, "Login token was not returned");
        Assert.assertNotNull(userId, "Login user id was not returned");

        // Step 2: Perform a booking using a post call.
        String cityId = fetchCityId("beng", "Bengaluru");
        String adventureId = fetchAdventureId(cityId, "Niaboytown");
        Response reservationResponse = createReservation(userId, guestName, travelDate, personCount,
                adventureId, token);

        // Step 3: Ensure that the booking goes fine and is listed in reservations.
        Assert.assertEquals(reservationResponse.getStatusCode(), 200,
                "Reservation API did not return status code 200");

        Response reservationsResponse = getReservations(userId, token);
        Assert.assertEquals(reservationsResponse.getStatusCode(), 200,
                "Reservations API did not return status code 200");
        Assert.assertTrue(reservationExists(reservationsResponse, guestName, travelDate, personCount,
                adventureId), "Created reservation was not found in reservations response");
    }
}
