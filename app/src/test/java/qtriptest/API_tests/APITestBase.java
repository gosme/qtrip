package qtriptest.API_tests;

import static io.restassured.RestAssured.given;

import java.util.UUID;

import org.json.JSONObject;
import org.testng.Assert;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

public abstract class APITestBase {
    protected static final String DEFAULT_API_BASE_URL = "https://qtrip-backend.labs.crio.do";
    protected static final String REGISTER_PATH = "/api/v1/register";
    protected static final String LOGIN_PATH = "/api/v1/login";
    protected static final String CITIES_PATH = "/api/v1/cities";
    protected static final String ADVENTURES_PATH = "/api/v1/adventures";
    protected static final String RESERVATIONS_PATH = "/api/v1/reservations";
    protected static final String RESERVATIONS_NEW_PATH = "/api/v1/reservations/new";

    protected String getApiBaseUrl() {
        String systemValue = System.getProperty("api.base.url");
        if (systemValue != null && !systemValue.trim().isEmpty()) {
            return systemValue.trim();
        }

        String environmentValue = System.getenv("QTRIP_API_BASE_URL");
        if (environmentValue != null && !environmentValue.trim().isEmpty()) {
            return environmentValue.trim();
        }

        return DEFAULT_API_BASE_URL;
    }

    protected Response postJson(String path, JSONObject payload) {
        return given()
                .baseUri(getApiBaseUrl())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(payload.toString())
                .when()
                .post(path)
                .then()
                .extract()
                .response();
    }

    protected Response getWithQueryParam(String path, String paramName, String value) {
        return given()
                .baseUri(getApiBaseUrl())
                .accept(ContentType.JSON)
                .queryParam(paramName, value)
                .when()
                .get(path)
                .then()
                .extract()
                .response();
    }

    protected Response getReservations(String userId, String token) {
        return given()
                .baseUri(getApiBaseUrl())
                .accept(ContentType.JSON)
                .header("Authorization", buildBearerToken(token))
                .header("x-access-token", token)
                .queryParam("id", userId)
                .when()
                .get(RESERVATIONS_PATH)
                .then()
                .extract()
                .response();
    }

    protected Response registerUser(String email, String password) {
        JSONObject payload = new JSONObject();
        payload.put("email", email);
        payload.put("password", password);
        payload.put("confirmpassword", password);
        return postJson(REGISTER_PATH, payload);
    }

    protected Response loginUser(String email, String password) {
        JSONObject payload = new JSONObject();
        payload.put("email", email);
        payload.put("password", password);
        return postJson(LOGIN_PATH, payload);
    }

    protected Response createReservation(String userId, String name, String date, String personCount,
            String adventureId, String token) {
        JSONObject payload = new JSONObject();
        payload.put("userId", userId);
        payload.put("name", name);
        payload.put("date", date);
        payload.put("person", personCount);
        payload.put("adventure", adventureId);

        return given()
                .baseUri(getApiBaseUrl())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", buildBearerToken(token))
                .header("x-access-token", token)
                .body(payload.toString())
                .when()
                .post(RESERVATIONS_NEW_PATH)
                .then()
                .extract()
                .response();
    }

    protected String buildUniqueEmail(String prefix) {
        return prefix + "+" + UUID.randomUUID() + "@gmail.com";
    }

    protected String extractToken(Response response) {
        return extractFirstNonBlank(response, "data.token", "token");
    }

    protected String extractUserId(Response response) {
        return extractFirstNonBlank(response, "data.id", "data.userId", "id", "userId");
    }

    protected boolean extractSuccess(Response response) {
        Object directValue = response.jsonPath().get("success");
        if (directValue instanceof Boolean) {
            return ((Boolean) directValue).booleanValue();
        }

        Object nestedValue = response.jsonPath().get("data.success");
        if (nestedValue instanceof Boolean) {
            return ((Boolean) nestedValue).booleanValue();
        }

        return false;
    }

    protected String fetchCityId(String cityQuery, String expectedCityName) {
        Response cityResponse = getWithQueryParam(CITIES_PATH, "q", cityQuery);
        Assert.assertEquals(cityResponse.getStatusCode(), 200, "City search did not succeed");

        for (Object item : cityResponse.jsonPath().getList("$")) {
            if (item instanceof java.util.Map) {
                java.util.Map<?, ?> cityMap = (java.util.Map<?, ?>) item;
                Object cityName = cityMap.get("city");
                if (cityName != null && expectedCityName.equalsIgnoreCase(String.valueOf(cityName))) {
                    Object id = cityMap.get("id");
                    if (id != null) {
                        return String.valueOf(id);
                    }
                }
            }
        }

        Assert.fail("Unable to find city id for " + expectedCityName);
        return null;
    }

    protected String fetchAdventureId(String cityId, String expectedAdventureName) {
        Response adventureResponse = getWithQueryParam(ADVENTURES_PATH, "city", cityId);
        Assert.assertEquals(adventureResponse.getStatusCode(), 200, "Adventure lookup did not succeed");

        for (Object item : adventureResponse.jsonPath().getList("$")) {
            if (item instanceof java.util.Map) {
                java.util.Map<?, ?> adventureMap = (java.util.Map<?, ?>) item;
                Object name = adventureMap.get("name");
                if (name != null && expectedAdventureName.equalsIgnoreCase(String.valueOf(name))) {
                    Object id = adventureMap.get("id");
                    if (id != null) {
                        return String.valueOf(id);
                    }
                }
            }
        }

        Assert.fail("Unable to find adventure id for " + expectedAdventureName);
        return null;
    }

    protected boolean reservationExists(Response reservationsResponse, String guestName, String date,
            String personCount, String adventureId) {
        for (Object item : reservationsResponse.jsonPath().getList("$")) {
            if (item instanceof java.util.Map) {
                java.util.Map<?, ?> reservationMap = (java.util.Map<?, ?>) item;
                String reservedName = toStringValue(reservationMap.get("name"));
                String reservedDate = toStringValue(reservationMap.get("date"));
                String reservedCount = toStringValue(reservationMap.get("person"));
                String reservedAdventureId = toStringValue(reservationMap.get("adventure"));
                String reservedCancelledState = toStringValue(reservationMap.get("isCancelled"));

                if (guestName.equalsIgnoreCase(reservedName)
                        && date.equalsIgnoreCase(reservedDate)
                        && personCount.equalsIgnoreCase(reservedCount)
                        && adventureId.equalsIgnoreCase(reservedAdventureId)
                        && !"true".equalsIgnoreCase(reservedCancelledState)) {
                    return true;
                }
            }
        }

        return false;
    }

    private String extractFirstNonBlank(Response response, String... paths) {
        for (String path : paths) {
            Object value = response.jsonPath().get(path);
            if (value != null && !String.valueOf(value).trim().isEmpty()) {
                return String.valueOf(value);
            }
        }

        return null;
    }

    private String buildBearerToken(String token) {
        return token == null ? "" : "Bearer " + token;
    }

    private String toStringValue(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
