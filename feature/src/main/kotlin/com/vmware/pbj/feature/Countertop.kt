package com.vmware.pbj.feature

class Countertop(
    private val ingredients: MutableSet<Platform> = mutableSetOf(),
): Platform {
    fun receive(bread: Bread) {
        this.ingredients.add(bread)
    }

    fun yield(bread: Bread) {
        this.ingredients.remove(bread)
    }

    override fun actions(): Map<String, (Actor) -> Unit> {
        return this.ingredients.fold(mapOf()) {
            acc, platform -> acc + platform.actions()
        }
    }
}
