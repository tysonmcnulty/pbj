Feature: Chef can unseal bread on the countertop

  Scenario: Bread is on the countertop
    Given there is bread on the countertop
    And the chef has access to the countertop
    When the chef acts to unseal the bread
    Then the bread is unsealed
