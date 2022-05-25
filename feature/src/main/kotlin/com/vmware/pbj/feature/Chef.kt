package com.vmware.pbj.feature

class Chef: Actor {
    private val platforms: MutableSet<Platform> = mutableSetOf()
    val possessions: MutableSet<Any> = mutableSetOf()

    fun receiveAccess(platform: Platform) {
        platforms.add(platform)
    }

    fun commands(): Map<String, () -> Unit> {
        return platforms.fold(mapOf()) {
            actions, platform -> actions + platform.actions().mapValues { action ->
               { action.value.invoke(this) }
            }
        }
    }

    override fun act(description: String) {
        commands()[description]?.invoke()
    }

    override fun receive(it: Any) {
        possessions.add(it)
    }

    fun relinquishAccess(platform: Platform) {
        platforms.remove(platform)
    }
}
