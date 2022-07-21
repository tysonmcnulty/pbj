package com.vmware.pbj.feature

import com.vmware.pbj.core.model.Slice as SliceModel
import com.vmware.pbj.core.model.Bread as BreadModel

class Bread(
    private var isSealed: Boolean = false,
): Platform, BreadModel() {

    var numberOfSlices: Int
        get() = this.slices.size
        set(value) {
            slices = ArrayDeque<SliceModel>(List(value) { Slice() })
        }

    init {
        numberOfSlices = 12
    }

    override fun actions(): Map<String, (Actor) -> Unit> {
        val universalActions = mapOf(
            "unseal bread" to this::unseal,
            "seal bread" to this::seal
        )

        val additionalActionsIfUnsealed = mapOf(
            "take a slice" to this::takeSlice
        )

        return when (!isSealed && numberOfSlices > 0) {
            false -> universalActions
            true -> universalActions + additionalActionsIfUnsealed
        }
    }

    private fun seal(actor: Actor) {
        this.isSealed = true
    }

    private fun unseal(actor: Actor) {
        this.isSealed = false
    }

    private fun takeSlice(actor: Actor) {
        (slices as ArrayDeque<SliceModel>).removeFirstOrNull()?.let {
            actor.receive(it)
        }
    }

    override fun isSealed(): Boolean {
        return isSealed
    }

    fun setSealed(isSealed: Boolean) {
        this.isSealed = isSealed
    }

    class Slice: SliceModel()
}
