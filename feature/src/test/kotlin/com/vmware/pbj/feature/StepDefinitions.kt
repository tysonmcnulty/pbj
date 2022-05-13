package com.vmware.pbj.feature

import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.junit.jupiter.api.Assertions.assertFalse

class StepDefinitions {

    private lateinit var countertop: Countertop
    private lateinit var bread: Bread
    private lateinit var chef: Chef

    @Given("^there is a countertop$")
    fun given_countertop() {
        countertop = Countertop()
    }

    @Given("^there is bread$")
    fun given_bread() {
        bread = Bread()
    }

    @Given("^there is a chef$")
    fun given_chef() {
        chef = Chef()
    }

    @Given("^the bread is on the countertop$")
    fun given_countertop_has_bread() {
        countertop.receive(bread)
    }

    @Given("^the bread is not on the countertop$")
    fun given_countertop_has_no_bread() {
        countertop.yield(bread)
    }

    @Given("^the chef has access to the countertop$")
    fun given_the_chef_has_accessed_the_countertop() {
        chef.receiveAccess(countertop)
    }

    @Given("^the bread is sealed$")
    fun given_the_bread_is_sealed() {
        bread.sealed = true
    }

    @When("^the chef acts to unseal the bread$")
    fun when_the_chef_acts_to_unseal_the_bread() {
        chef.act("unseal bread")
    }

    @Then("^the bread is unsealed$")
    fun then_the_bread_is_unsealed() {
        assertFalse(bread.sealed)
    }
}
