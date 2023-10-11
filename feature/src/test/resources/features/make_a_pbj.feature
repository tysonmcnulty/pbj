Feature: Kitchen

  Rule: Making a PBJ

    Background:
      * There is a loaf of bread called the "bread"
      * There is a jar of jelly called the "jelly jar"
      * There is a jar of peanut butter called the "peanut butter jar"

    Scenario:
      When I make a PBJ using the bread, the jelly jar, and the peanut butter jar
      Then I have a PBJ
