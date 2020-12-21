Feature: Current weather api functionality

  #GET
  Scenario Outline: current weather by CityId in different metrics
    Given I have a city "<city>" with city id as "<cityId>"
    When I search for the current weather with "<cityId>" and "<units>"
    Then I should be displayed the min and max temperature in "<scale>"
    Examples:
      |city  | cityId   |units  | scale   |
      |Paris |  2968815 | metric| Celsius  |
      |Paris |  2968815 | standard| Kelvin  |
      |Paris |  2968815 | imperial| Fahrenheit |

    #GET
    Scenario Outline: hottest day of a city in a week
    Given I have a city "<city>" with "<lat>" and "<lon>"
    When I search weather forecast for a week with "<exclude>" params
    Then I should be displayed the hottest day for that city
      Examples:
        | city       | lat        | lon     | exclude  |
        |  Paris     | 48.853401  |  2.3486 |current,minutely,alerts,hourly|
        |Edinburgh   |55.94973    |-3.19333 |current,minutely,alerts,hourly|
        |Glasgow City|55.866669   |-4.25    |current,minutely,alerts,hourly|
        |Madrid      |40.489349   |-3.68275 |current,minutely,alerts,hourly|
        |Berlin Schoeneberg| 52.474571|13.34839 |current,minutely,alerts,hourly|