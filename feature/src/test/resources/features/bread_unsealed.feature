Feature: Kitchen

  Rule: Chef unseals bread on a countertop

    Background:
      * there is bread
      * the bread is sealed
      * there is a chef
      * there is a countertop

    Scenario: Chef can unseal bread
      Given the bread is on the countertop
      And the chef has access to the countertop
      Then the chef can unseal the bread

    Scenario Template: Chef cannot unseal bread
      Given the bread <bread ~ countertop> the countertop
      And the chef <chef ~ countertop> the countertop
      Then the chef cannot unseal the bread

      Examples:
        | bread ~ countertop | chef ~ countertop       |
        | is not on          | has access to           |
        | is on              | does not have access to |
        | is not on          | does not have access to |

    Scenario: Bread is unsealed
      Given the bread is on the countertop
      And the chef has access to the countertop
      When the chef unseals the bread
      Then the bread is unsealed

    Scenario Template: Bread is still sealed
      Given the bread <bread ~ countertop> the countertop
      And the chef <chef ~ countertop> the countertop
      When the chef tries to unseal the bread
      Then the bread is still sealed

      Examples:
        | bread ~ countertop | chef ~ countertop       |
        | is not on          | has access to           |
        | is on              | does not have access to |
        | is not on          | does not have access to |

  Rule: Chef takes a slice of bread

    Background:
      * there is bread
      * there is a chef
      * there is a countertop
      * the bread is on the countertop
      * the chef is holding nothing
      * the chef has access to the countertop

    Scenario:
      Given the bread is unsealed
      And the bread has 0 slices
      Then the chef cannot take a slice of bread

    Scenario:
      Given the bread is unsealed
      And the bread has 5 slices
      Then the chef can take a slice of bread

    Scenario:
      Given the bread is unsealed
      And the bread has 5 slices
      When the chef takes a slice of bread
      Then the chef is holding a slice of bread
      And the bread has 4 slices remaining

    Scenario:
      Given the bread is unsealed
      And the bread has 0 slices
      When the chef takes a slice of bread
      Then the chef is not holding a slice of bread
      And the bread has 0 slices remaining
