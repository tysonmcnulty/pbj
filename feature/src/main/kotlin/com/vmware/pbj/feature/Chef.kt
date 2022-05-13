package com.vmware.pbj.feature

class Chef(
    private val platforms: MutableSet<Platform> = mutableSetOf()
): Actor {
    fun receiveAccess(countertop: Countertop) {
        platforms.add(countertop)
    }

    private fun actions(): Map<String, () -> Unit> {
        val actions = mutableMapOf<String, () -> Unit>()
        platforms.fold(actions) { acc, platform ->
            acc += platform.actions()
            return acc
        }

        return actions
    }

    override fun act(description: String) {
        actions()[description]?.invoke()
    }
}
