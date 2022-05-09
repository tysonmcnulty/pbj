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
        if (::countertop.isInitialized) return
        countertop = Countertop()
    }

    @Given("^there is bread$")
    fun given_bread() {
        if (::bread.isInitialized) return
        bread = Bread()
    }

    @Given("^there is a chef$")
    fun given_chef() {
        if (::chef.isInitialized) return
        chef = Chef()
    }

    @Given("^there is bread on the countertop$")
    fun given_countertop_has_bread() {
        given_bread()
        given_countertop()
        countertop.receive(bread)
    }

    @Given("^the chef has access to the countertop")
    fun given_the_chef_has_accessed_the_countertop() {
        given_countertop()
        given_chef()
        chef.receiveAccess(countertop)
    }

    @When("^the chef acts to unseal the bread$")
    fun when_the_chef_acts_to_unseal_the_bread() {
        given_chef()
        chef.act("unseal bread")
    }

    @Then("^the bread is unsealed$")
    fun then_the_bread_is_unsealed() {
        assertFalse(bread.sealed)
    }
}
