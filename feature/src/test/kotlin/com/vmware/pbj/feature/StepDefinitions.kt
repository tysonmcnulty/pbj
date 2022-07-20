package com.vmware.pbj.feature

import com.foo.bar.Blaggy
import com.foo.bar.Stooky
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue

class StepDefinitions {

    private lateinit var countertop: Countertop
    private lateinit var bread: Bread
    private lateinit var chef: Chef
    private val blaggy: Blaggy = Blaggy()

    @Given("^there is a countertop$")
    fun given_countertop() {
        countertop = Countertop()
    }

    @Given("^there is bread$")
    fun given_bread() {
        bread = Bread()
    }

    @Given("^the bread is on the countertop$")
    fun given_countertop_has_bread() {
        countertop.receive(bread)
    }

    @Given("^the bread is not on the countertop$")
    fun given_countertop_has_no_bread() {
        countertop.yield(bread)
    }

    @Given("^the bread is sealed$")
    fun given_the_bread_is_sealed() {
        bread.sealed = true
    }

    @Given("^the bread has (\\d+) slices")
    fun given_bread_has_number_of_slices(numSlices: Int) {
        bread.numberOfSlices = numSlices
    }

    @Given("^there is a chef$")
    fun given_chef() {
        chef = Chef()
    }

    @Given("^the chef is holding nothing")
    fun the_chef_is_holding_nothing() {
        chef.possessions.clear()
    }

    @Given("^the chef has access to the countertop$")
    fun given_countertop_access() {
        chef.receiveAccess(countertop)
    }

    @Given("^the chef does not have access to the countertop$")
    fun given_no_countertop_access() {
        chef.relinquishAccess(countertop)
    }

    @When("^the chef (unseals|tries to unseal) the bread$")
    fun when_the_chef_unseals_the_bread(which: String) {
        chef.act("unseal bread")
    }

    @When("^the chef takes a slice of bread")
    fun when_the_chef_takes_a_slice_of_bread() {
        chef.act("take a slice")
    }

    @Then("^the chef can unseal the bread")
    fun then_the_chef_can_unseal_the_bread() {
        assertThat(chef.commands().keys, hasItem("unseal bread"))
    }

    @Then("^the chef cannot unseal the bread")
    fun then_the_chef_cannot_unseal_the_bread() {
        assertThat(chef.commands().keys, not(hasItem(("unseal bread"))))
    }

    @Then("^the chef can take a slice of bread")
    fun then_the_chef_can_take_a_slice_of_bread() {
        assertThat(chef.commands().keys, hasItem("take a slice"))
    }

    @Then("^the chef cannot take a slice of bread")
    fun then_the_chef_cannot_take_a_slice_of_bread() {
        assertThat(chef.commands().keys, not(hasItem(("take a slice"))))
    }

    @Then("^the chef is holding a slice of bread")
    fun then_the_chef_is_holding_a_slice_of_bread() {
        assertThat(chef.possessions, hasItem(isA(Bread.Slice::class.java)))
    }

    @Then("^the chef is not holding a slice of bread")
    fun then_the_chef_is_not_holding_a_slice_of_bread() {
        assertThat(chef.possessions, not(hasItem(isA(Bread.Slice::class.java))))
    }

    @Then("^the bread is unsealed$")
    fun then_the_bread_is_unsealed() {
        assertFalse(bread.sealed)
    }

    @Then("^the bread is (still)? sealed")
    fun then_the_bread_is_sealed(which: String) {
        assertTrue(bread.sealed)
    }

    @Then("^the bread has (\\d+) slices remaining")
    fun then_bread_slices_remaining(numSlices: Int) {
        assertThat(bread.numberOfSlices, equalTo(numSlices))
    }
}
