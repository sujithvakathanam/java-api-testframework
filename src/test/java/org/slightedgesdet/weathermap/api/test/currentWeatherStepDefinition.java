package org.slightedgesdet.weathermap.api.test;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class currentWeatherStepDefinition {
    private static final String CurrentWeather_BASE_URL = "https://api.openweathermap.org/data/2.5/" + "weather?";
    private static final String OneCall_BASE_URL = "https://api.openweathermap.org/data/2.5/" + "onecall?";
    private static final String API_KEY = "c825951bd8a11a053cb91a6eb13d3200";
    private final TestContext testContext = TestContext.getInstance();
    private String cityName;
    private String cityId;
    private String units;
    private String scale;
    private String lon;
    private String lat;
    private String exclude;

    @Given("I have a city {string} with city id as {string}")
    public void i_have_a_city_with_city_id_as(String cityName, String cityId){
        //https://api.openweathermap.org/data/2.5/weather?id=2968815&appid=c825951bd8a11a053cb91a6eb13d3200&units=metric
        this.cityName = cityName;
        this.cityId = cityId;
    }

    @Given("I have a city {string} with {string} and {string}")
    public void i_have_a_city_with_lat_and_long(String cityName, String lat, String lon){
        this.cityName = cityName;
        this.lat = lat;
        this.lon = lon;
    }

    @When("I search for the current weather with {string} and {string}")
    public void i_search_for_the_current_weather_with_cityid_and_units(String cityId, String units) throws IOException {
        this.units = units;
        HttpGet request = new HttpGet(CurrentWeather_BASE_URL+ "id=" + this.cityId + "&appid=" + API_KEY + "&units=" + this.units);
        this.testContext.execute(request, 200);
    }

    @When("I search weather forecast for a week with {string} params")
    public void i_search_for_weather_forecast_for_a_week_with_exclude_params(String exclude) throws IOException, InterruptedException {
        // https://api.openweathermap.org/data/2.5/onecall?lat=33.441792&lon=-94.037689&exclude=current,minutely,alerts,hourly&appid=c825951bd8a11a053cb91a6eb13d3200
        this.exclude = exclude;
        HttpGet request = new HttpGet(OneCall_BASE_URL + "lat=" + this.lat + "&lon=" + this.lon + "&exclude=" +this.exclude + "&appid=" + API_KEY );
        this.testContext.executeWithRetry(request, 200);
    }

    @Then("I should be displayed the min and max temperature in {string}")
    public void i_should_be_displayed_the_min_and_max_temperature_in_scale(String scale) {
        this.scale = scale;
        assertEquals(200, this.testContext.getLastResponse().getStatusLine().getStatusCode());
        this.testContext.displayTemperature(this.cityName, this.scale);
    }

    @Then("I should be displayed the hottest day for that city")
    public void i_should_be_displayed_the_hottest_day_for_that_city() {
        assertEquals(200, this.testContext.getLastResponse().getStatusLine().getStatusCode());
        this.testContext.displayHottestDay(this.cityName);
    }

}
