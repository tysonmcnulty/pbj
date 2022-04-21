Feature: Chef can unseal bread on the countertop

  Scenario: Bread is on the countertop
    Given bread is on the countertop
    When the chef unseals it
    Then the bread is unsealed

