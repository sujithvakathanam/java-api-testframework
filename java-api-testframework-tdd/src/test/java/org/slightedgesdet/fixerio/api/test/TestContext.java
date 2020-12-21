package org.slightedgesdet.fixerio.api.test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestContext {
    private static final TestContext instance = new TestContext();
    private HttpResponse lastResponse;
    private String responseBody;
    private String lastResponseBody;

    public TestContext() {
    }

    public static TestContext getInstance() {
        return instance;
    }

    public void execute(HttpUriRequest request, int expectedHttpStatus) throws IOException {
        HttpResponse response = HttpClients.createDefault().execute(request);
        responseBody = new String(response.getEntity().getContent().readAllBytes());
        assertEquals(expectedHttpStatus, response.getStatusLine().getStatusCode());
        this.lastResponse = response;
        this.lastResponseBody = responseBody;
    }

    public HttpResponse getLastResponse() {
        return this.lastResponse;
    }

    public void assertRates(Map<String, Double> expectedRates) {
        assertEquals(200, this.lastResponse.getStatusLine().getStatusCode());
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(lastResponseBody, JsonObject.class);
        JsonObject rates = jsonObject.get("rates").getAsJsonObject();
        assertEquals(expectedRates.get("USD"), rates.get("USD").getAsDouble());
        assertEquals(expectedRates.get("AUD"), rates.get("AUD").getAsDouble());
        assertEquals(expectedRates.get("CAD"), rates.get("CAD").getAsDouble());
        assertEquals(expectedRates.get("PLN"), rates.get("PLN").getAsDouble());
        assertEquals(expectedRates.get("MXN"), rates.get("MXN").getAsDouble());
    }

    public void displayLatestRates() {
        assertEquals(200, this.lastResponse.getStatusLine().getStatusCode());
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(lastResponseBody, JsonObject.class);
        JsonObject ratesObject = jsonObject.get("rates").getAsJsonObject();
        System.out.println("Current Exchange rate for GBP corresponding to Eur base Currency is :" +
                ratesObject.get("GBP").getAsDouble());
        System.out.println("Current Exchange rate for JPY corresponding to Eur base Currency is :" +
                ratesObject.get("JPY").getAsDouble());
        System.out.println("Current Exchange rate for USD corresponding to Eur base Currency is :" +
                ratesObject.get("USD").getAsDouble());
    }
}
