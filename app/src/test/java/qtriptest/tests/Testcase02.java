package qtriptest.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import qtriptest.DP;
import qtriptest.pages.AdventurePage;
import qtriptest.pages.HomePage;

public class Testcase02 extends BaseTest {
    @Test(
            dataProvider = "data-provider",
            dataProviderClass = DP.class,
            groups = { "Search and Filter flow" },
            priority = 2,
            enabled = true,
            description = "Test Case 2: Verify functionality of Search and Filters")
    public void TestCase02(String cityName, String categoryFilter, String durationFilter,
            String expectedFilteredResults, String expectedUnFilteredResults) {
        HomePage homePage = new HomePage(driver);

        // Step 1: Navigate to the Home page of QTrip.
        Assert.assertTrue(homePage.getTitle().toLowerCase().contains("qtrip"), "Home page title did not load");
        Assert.assertTrue(homePage.isPageLoaded(), "Home page was not fully loaded");

        // Step 2 and 3: Search for a city that is not present and verify the invalid city message.
        homePage.searchCity("Invalid-City-For-QTrip");
        Assert.assertTrue(homePage.waitForInvalidCityMessage(), "Invalid city message was not displayed");

        // Step 4 and 5: Search for a city that is present and verify it appears in autocomplete.
        homePage.searchCity(cityName);
        Assert.assertTrue(homePage.isValidCityDisplayedInAutoComplete(cityName),
                "Expected city was not displayed in autocomplete");

        // Step 6: Click on the city.
        homePage.clickOnCity(cityName);

        AdventurePage adventurePage = new AdventurePage(driver);

        // Step 7 and 8: Select duration filter and verify filtered data is displayed.
        adventurePage.selectFilters(durationFilter);
        Assert.assertTrue(adventurePage.verifyDataDisplayed(), "No data displayed after duration filter");

        // Step 9 and 10: Select category filter and verify filtered data is displayed.
        adventurePage.selectCategory(categoryFilter);
        Assert.assertTrue(adventurePage.verifyDataDisplayed(), "No data displayed after category filter");
        Assert.assertEquals(String.valueOf(adventurePage.getAdventureCount()), expectedFilteredResults,
                "Filtered result count does not match expected");

        // Step 11 and 12: Clear filters and verify all records are displayed.
        adventurePage.clearFilters();
        adventurePage.clearCategory();
        Assert.assertEquals(String.valueOf(adventurePage.getAdventureCount()), expectedUnFilteredResults,
                "Unfiltered result count does not match expected");
    }
}
