Feature: Kitchen

  Rule: Making a PBJ

    Background:
      * There is a loaf of bread called the "bread"
      * There is a jar called the "jelly jar", which has jelly
      * There is a jar called the "peanut butter jar", which has peanut butter

    Scenario:
      When I make a PBJ using the bread, the jelly jar, and the peanut butter jar
      Then I have a PBJ
