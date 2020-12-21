package org.slightedgesdet.fixerio.api.test;

import org.apache.http.client.methods.HttpGet;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class APITest {
    private static final String BASE_URL = "http://data.fixer.io/api/";
    private static final String ACCESS_KEY = "1d79592a9d4f8863ef72f79222b431cc";
    TestContext testContext = new TestContext();

    @Test
    public void historicalRates() throws IOException {
    //Arrange
    String date= "2020-11-30";
    String currencies = "USD,AUD,CAD,PLN,MXN";
    Map<String, Double> expectedRates = new HashMap<>();
    expectedRates.put("USD", 1.19378);
    expectedRates.put("AUD", 1.622734);
    expectedRates.put("CAD", 1.551001);
    expectedRates.put("PLN", 4.479333);
    expectedRates.put("MXN", 24.071979);

    //Act
    //http://data.fixer.io/api/2020-11-30?access_key=1d79592a9d4f8863ef72f79222b431cc&symbols=USD,AUD,CAD,PLN,MXN
    HttpGet request = new HttpGet(BASE_URL + date + "?access_key=" + ACCESS_KEY+ "&symbols=" + currencies);
    this.testContext.execute(request,200);

    //Assert
    assertEquals(200, this.testContext.getLastResponse().getStatusLine().getStatusCode());
    this.testContext.assertRates(expectedRates);
    }

    @Test
    public void latestRatesEndpoint() throws IOException {
        //Arrange
        String base = "EUR";
        String symbols = "GBP,JPY,USD";

        //Act
        //http://data.fixer.io/api/latest?access_key=1d79592a9d4f8863ef72f79222b431cc&base=EUR&symbols=GBP,JPY,USD
        HttpGet request = new HttpGet(BASE_URL + "latest?access_key=" + ACCESS_KEY + "&base=" + base + "&symbols=" + symbols);
        this.testContext.execute(request, 200);

        //Assert
        assertEquals(200, this.testContext.getLastResponse().getStatusLine().getStatusCode());
        this.testContext.displayLatestRates();
    }
}
