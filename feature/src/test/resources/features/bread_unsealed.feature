Feature: Countertop

  Rule: The chef can unseal sealed bread on an accessed countertop

    Background:
      * there is bread
      * the bread is sealed
      * there is a chef
      * there is a countertop

    Scenario Template:
      Given the bread is <where>
      And the chef <relationship> the countertop
      When the chef unseals the bread
      Then the bread <result>

      Examples:
        | where                 | relationship            | result      |
        | on the countertop     | has access to           | is unsealed |
        | not on the countertop | has access to           | is sealed   |
        | on the countertop     | does not have access to | is sealed   |
        | not on the countertop | does not have access to | is sealed   |
