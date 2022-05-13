Feature: Countertop

  Rule: The chef can interact with bread on a countertop

    Background:
      * there is bread
      * there is a chef
      * there is a countertop

    Scenario: Sealed, on countertop, accessed
      Given the bread is on the countertop
      And the bread is sealed
      And the chef has access to the countertop
      When the chef acts to unseal the bread
      Then the bread is unsealed

    Scenario: Sealed, not on countertop, accessed
      Given the bread is not on the countertop
      And the bread is sealed
      And the chef has access to the countertop
      When the chef acts to unseal the bread
      Then the bread is sealed
