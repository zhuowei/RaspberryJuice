Feature: Calculate the relative locations as different config options and pi/bukkit can provide different locations

  Scenario Outline: Test the locationToRelative function

    Given The location type <LocationType>
    And a spawn point of <SpawnX>, <SpawnY>, <SpawnZ>
    And a location point of 20, 3, -5
    When a request for a relative location is made
    Then the relative location should have co-ordinates <LocationX>, <LocationY>, <LocationZ>

    Examples:
      | LocationType | SpawnX | SpawnY | SpawnZ | LocationX | LocationY | LocationZ |
      | RELATIVE     | 0      | 0      | 0      | 20.0      | 3.0       | -5.0      |
      | RELATIVE     | -100   | 50     | 100    | 120.0     | -47.0     | -105.0    |
      | ABSOLUTE     | 0      | 0      | 0      | 20.0      | 3.0       | -5.0      |
      | ABSOLUTE     | -100   | 50     | 100    | 20.0      | 3.0       | -5.0      |

  Scenario Outline: Determine the location of co-ordinates relative to a block

    Given The location type <LocationType>
    And a spawn point of <SpawnX>, <SpawnY>, <SpawnZ>
    When a request for a relative block location is made at 32, 69, -100
    Then the block location should have co-ordinates <LocationX>, <LocationY>, <LocationZ>

    Examples:
      | LocationType | SpawnX | SpawnY | SpawnZ | LocationX | LocationY | LocationZ |
      | RELATIVE     | 0      | 0      | 0      | 32        | 69        | -100      |
      | RELATIVE     | -100   | 50     | 100    | -68       | 119       | 0         |
      | ABSOLUTE     | 0      | 0      | 0      | 32        | 69        | -100      |
      | ABSOLUTE     | -100   | 50     | 100    | 32        | 69        | -100      |

  Scenario Outline: Determine the location of co-ordinates

    Given The location type <LocationType>
    And a spawn point of <SpawnX>, <SpawnY>, <SpawnZ>
    When a request for a relative location is made at 32, 69, -100
    Then the location should have co-ordinates <LocationX>, <LocationY>, <LocationZ>

    Examples:
      | LocationType | SpawnX | SpawnY | SpawnZ | LocationX | LocationY | LocationZ |
      | RELATIVE     | 0      | 0      | 0      | 32        | 69        | -100      |
      | RELATIVE     | -100   | 50     | 100    | -68       | 119       | 0         |
      | ABSOLUTE     | 0      | 0      | 0      | 32        | 69        | -100      |
      | ABSOLUTE     | -100   | 50     | 100    | 32        | 69        | -100      |
