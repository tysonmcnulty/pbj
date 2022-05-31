package com.vmware.pbj.feature

class Bread(
    var sealed: Boolean = false,
    numberOfSlices: Int = 12
): Platform {
    private var slices: ArrayDeque<Slice> = ArrayDeque(List(numberOfSlices) { Slice() })

    var numberOfSlices: Int
        get() = this.slices.size
        set(value) {
            slices = ArrayDeque(List(value) { Slice() })
        }

    override fun actions(): Map<String, (Actor) -> Unit> {
        val universalActions = mapOf(
            "unseal bread" to this::unseal,
            "seal bread" to this::seal
        )

        val additionalActionsIfUnsealed = mapOf(
            "take a slice" to this::takeSlice
        )

        return when (!sealed && numberOfSlices > 0) {
            false -> universalActions
            true -> universalActions + additionalActionsIfUnsealed
        }
    }

    private fun seal(actor: Actor) {
        this.sealed = true
    }

    private fun unseal(actor: Actor) {
        this.sealed = false
    }

    private fun takeSlice(actor: Actor) {
        slices.removeFirstOrNull()?.let {
            actor.receive(it)
        }
    }

    class Slice
}
