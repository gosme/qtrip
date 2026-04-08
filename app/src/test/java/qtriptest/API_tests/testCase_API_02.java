package qtriptest.API_tests;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.response.Response;

public class testCase_API_02 extends APITestBase {
    @Test(groups = { "API Tests" }, priority = 2,
            description = "Verify that the search City API Returns the correct number of results")
    public void testCase_API_02() {
        // Step 1: Search for "beng" using the cities search API.
        Response citySearchResponse = getWithQueryParam(CITIES_PATH, "q", "beng");

        // Step 2: Verify the count of results being returned and validate the schema.
        Assert.assertEquals(citySearchResponse.getStatusCode(), 200, "City search API did not return status code 200");
        citySearchResponse.then().assertThat().body(matchesJsonSchemaInClasspath("schemas/cities-search-schema.json"));
        Assert.assertEquals(citySearchResponse.jsonPath().getList("$").size(), 1,
                "City search result length did not match expected count");
        Assert.assertTrue(citySearchResponse.jsonPath().getString("[0].description").contains("100+ Places"),
                "City description did not contain the expected text");
    }
}
