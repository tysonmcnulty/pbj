package io.github.tysonmcnulty.pbj.feature

import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.github.tysonmcnulty.pbj.feature.core.model.*
import org.hamcrest.CoreMatchers.*
import kotlin.test.assertNotNull

class StepDefinitions {

    private lateinit var jellyJar: Jar<Jelly>
    private lateinit var peanutButterJar: Jar<PeanutButter>
    private lateinit var bread: Bread.Loaf
    private lateinit var pbj: PBJ

    @Given("There is a loaf of bread called the \"bread\"")
    fun given_the_bread() {
        bread = Bread.Loaf()
    }

    @Given("There is a jar of jelly called the \"jelly jar\"")
    fun given_the_jelly_jar() {
        jellyJar = Jar<Jelly>()
    }

    @Given("There is a jar of peanut butter called the \"peanut butter jar\"")
    fun given_the_peanut_butter_jar() {
        peanutButterJar = Jar<PeanutButter>()
    }

    @When("I make a PBJ using the bread, the jelly jar, and the peanut butter jar")
    fun when_make_PBJ() {
        pbj = makePbj(bread, jellyJar, peanutButterJar);
    }

    @Then("I have a PBJ")
    fun then_PBJ() {
        assertNotNull(pbj)
    }
}
