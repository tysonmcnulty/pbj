package com.vmware.pbj.feature

class Bread(
    var sealed: Boolean = false,
    var numberOfSlices: Int = 12
): Platform {

    override fun actions(): Map<String, (Actor) -> Unit> {
        return mapOf(
            "unseal bread" to this::unseal,
            "seal bread" to this::seal,
            "take a slice" to this::takeSlice,
        )
    }

    private fun seal(actor: Actor) {
        this.sealed = true
    }

    private fun unseal(actor: Actor) {
        this.sealed = false
    }

    private fun takeSlice(actor: Actor) {
        this.numberOfSlices--
        actor.receive(Slice())
    }

    class Slice
}
