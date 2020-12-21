package org.slightedgesdet.maprequest.api.test;

import com.google.gson.JsonArray;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;

import java.util.List;

public class openDirectionsStepDefinition {
    private String fromLocation;
    private String toLocation;

    @Given("I have the from and To Locations as below")
    public void i_have_the_from_and_to_locations_as_below(DataTable table){
        // 1. Table converted to List of Lists
        List<List<String>> rows = table.asLists(String.class);
        JsonArray locationsArray = new JsonArray();
        for(List<String> row: rows){
            this.fromLocation = row.get(0);
            this.toLocation = row.get(1);
            locationsArray.add(this.fromLocation);
            locationsArray.add(this.toLocation);
        }
        System.out.println("fromlocation: " + locationsArray.get(0));
        System.out.println("fromlocation: " + locationsArray.get(1));
    }

}
