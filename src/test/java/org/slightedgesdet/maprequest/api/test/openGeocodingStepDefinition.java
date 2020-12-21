package org.slightedgesdet.maprequest.api.test;

import com.google.gson.JsonObject;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.slightedgesdet.weathermap.api.test.TestContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class openGeocodingStepDefinition {
    private static final String GeoCode_BASE_URL = "http://open.mapquestapi.com/" + "geocoding/";
    private static final String APIKEY = "zdMQ26YAmJgBrrDDVATqGQw35EoKrYYR";
    private final TestContext testContext = TestContext.getInstance();

    private String location;

    @Given("I have a location {string}")
    public void i_have_a_location(String location){
        this.location = location;
    }

    @When("I request the geocode address")
    public void i_request_the_geocode_address() throws IOException {
        HttpPost request = new HttpPost(GeoCode_BASE_URL + "v1/address?key=" + APIKEY);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("location", this.location));
        JsonObject jsonObject= new JsonObject();
        jsonObject.addProperty("thumbMaps",false);
        nameValuePairs.add(new BasicNameValuePair("options", jsonObject.toString()));
        request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        request.addHeader("content-type", "application/json");
        this.testContext.execute(request, 200);
    }

    @Then("I should be displayed with data as")
    public void i_Should_Be_Displayed_With_Data_As(DataTable table) {
        // 2. Table converted to map object
        List<Map<String, String>> rows = table.asMaps(String.class, String.class);

        for(Map<String, String> columns: rows){
            this.testContext.assertLatLng(columns.get("geoCodeQuality"), columns.get("lat"), columns.get("lng"));
        }
    }

    @Then("I should be displayed with {string}, {string}, {string}")
    public void iShouldBeDisplayedWith(String geoCodeQuality, String lat, String lng) {
        this.testContext.assertLatLng(geoCodeQuality, lat, lng);
    }
}
