package org.slightedgesdet.weathermap.api.test;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestContext {
    private static final TestContext instance = new TestContext();
    private static final int RETRY_NR_OF_TIMES = 3;
    private static final int RETRY_INTERVAL_IN_SECONDS = 30;

    private HttpResponse lastResponse;
    private String responseBody;
    private String lastResponseBody;

    public TestContext() {
    }

    public static TestContext getInstance(){
        return instance;
    }

    public void execute(HttpUriRequest request, int expectedHttpStatus) throws IOException {
        HttpResponse response = HttpClients.createDefault().execute(request);
        responseBody = new String(response.getEntity().getContent().readAllBytes());
        assertEquals(expectedHttpStatus, response.getStatusLine().getStatusCode());
        this.lastResponse = response;
        this.lastResponseBody = responseBody;
    }

    public void executeWithRetry(HttpUriRequest request, int expectedHttpStatus) throws IOException, InterruptedException {
        HttpResponse response = HttpClients.createDefault().execute(request);
        int retryCount = 0;
        while (this.statusIsNotAsExpected(response, expectedHttpStatus) && this.retriesNotExhausted(retryCount)){
            this.printRetry(request, response, expectedHttpStatus, retryCount + 1);
            TimeUnit.SECONDS.sleep(RETRY_INTERVAL_IN_SECONDS);
            response = HttpClients.createDefault().execute(request);
            retryCount++;
        }
        this.lastResponse = response;
        this.lastResponseBody = new String(this.lastResponse.getEntity().getContent().readAllBytes());
    }

    private void printRetry(HttpUriRequest request, HttpResponse response, int expectedHttpStatus, int retryCount) {
        System.out.println("Request to " + request.getURI() + " has unexpected response status, expected was  "+
                expectedHttpStatus+ "and actual status was " + response.getStatusLine().getStatusCode()+
                "executing retry nr: " + retryCount + "/" + RETRY_NR_OF_TIMES + " in " + RETRY_INTERVAL_IN_SECONDS + " seconds");
    }

    private boolean retriesNotExhausted(int retryCount) {
        return retryCount < RETRY_NR_OF_TIMES;
    }

    private boolean statusIsNotAsExpected(HttpResponse response, int expectedHttpStatus) {
        return response.getStatusLine().getStatusCode() != expectedHttpStatus;
    }

    public String getLastResponseBodyAsString() {
        return this.lastResponseBody;
    }

    public HttpResponse getLastResponse() {
        return this.lastResponse;
    }

    public void displayTemperature(String cityName, String scale) {
        assertEquals(200,this.lastResponse.getStatusLine().getStatusCode());
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(lastResponseBody,JsonObject.class);

        JsonObject mainObject = jsonObject.get("main").getAsJsonObject();
        System.out.printf("\nMin temperature in %s is %f %s",  cityName, mainObject.get("temp_min").getAsDouble(), scale);
        System.out.printf("\nMax temperature in %s is %f %s",  cityName, mainObject.get("temp_max").getAsDouble(), scale);
    }

    public void displayHottestDay(String cityName) {
        assertEquals(200, this.lastResponse.getStatusLine().getStatusCode());
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(lastResponseBody, JsonObject.class);

        JsonArray jsonArray = jsonObject.get("daily").getAsJsonArray();
        ArrayList<JsonObject> tempObjects = new ArrayList<>();
        jsonArray.forEach(jsonElement -> tempObjects.add(jsonElement.getAsJsonObject().get("temp").getAsJsonObject()));

        double maxTemp = 0.0;
        JsonObject temp = new JsonObject();
        for (JsonObject item : tempObjects) {
            if (item.get("max").getAsDouble() > maxTemp) {
                maxTemp = item.get("max").getAsDouble();
                temp = item;
            }
        }

        long unixTime = 0;
        
        for(JsonElement jsonElement : jsonArray){
            if(jsonElement.getAsJsonObject().get("temp").getAsJsonObject().equals(temp)){
                unixTime = jsonElement.getAsJsonObject().get("dt").getAsLong();
            }
        }

        UnixDateTimeToDate(cityName, unixTime);
    }

    public void assertLatLng(String geoCodeQuality, String lat, String lng) {
        assertEquals(200, this.lastResponse.getStatusLine().getStatusCode());
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(lastResponseBody, JsonObject.class);
        JsonArray resultsJsonArray = jsonObject.get("results").getAsJsonArray();
        JsonArray locationsJsonArray = resultsJsonArray.get(0).getAsJsonObject().getAsJsonArray("locations").getAsJsonArray();

        for (JsonElement jsonElement : locationsJsonArray) {
            String actualGeoCodeQuality = jsonElement.getAsJsonObject().get("geocodeQuality").getAsString();
            if (actualGeoCodeQuality.equals(geoCodeQuality)) {
                Double actualLat = jsonElement.getAsJsonObject().get("latLng").getAsJsonObject().get("lat").getAsDouble();
                assertEquals(Double.valueOf(lat), actualLat);
                Double actualLng = jsonElement.getAsJsonObject().get("latLng").getAsJsonObject().get("lng").getAsDouble();
                assertEquals(Double.valueOf(lng), actualLng);
                System.out.println("Actual lat: " + actualLat);
                System.out.println("Actual lng: " + actualLng);
            }
        }
    }

    private void UnixDateTimeToDate(String cityName, long date) {
        final DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd ");

        final String formattedDtm = Instant.ofEpochSecond(date)
                .atZone(ZoneId.of("GMT-4"))
                .format(formatter);

        System.out.println("\nHottest day for city " + cityName + " : " + formattedDtm);
    }
}