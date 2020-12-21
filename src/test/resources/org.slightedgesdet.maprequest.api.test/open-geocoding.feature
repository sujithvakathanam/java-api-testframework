Feature: Open GeoCoding api functionality

  #POST
  Scenario: Get Geocode address
    Given I have a location "Washington,DC"
    When I request the geocode address
    Then I should be displayed with data as
    |geoCodeQuality|lat      |lng       |
    |CITY          | 38.894955  |-77.036646|

    #POST
   Scenario Outline: Get GeoCode Address for multiple cities
     Given I have a location "<location>"
     When I request the geocode address
     Then I should be displayed with "<geoCodeQuality>", "<lat>", "<lng>"
     Examples:
       |location            |geoCodeQuality|lat         |lng       |
       |  Washington,DC     |CITY          | 38.894955  |-77.036646|
       |  City of London,GB |CITY          | 51.515618  |-0.091998 |

