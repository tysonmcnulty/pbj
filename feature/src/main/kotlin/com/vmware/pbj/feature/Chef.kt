package com.vmware.pbj.feature

class Chef(
    private val platforms: MutableSet<Platform> = mutableSetOf()
): Actor {
    fun receiveAccess(platform: Platform) {
        platforms.add(platform)
    }

    private fun actions(): Map<String, () -> Unit> {
        return platforms.fold(mapOf()) { acc, platform ->
            acc + platform.actions()
        }
    }

    override fun act(description: String) {
        actions()[description]?.invoke()
    }

    fun relinquishAccess(platform: Platform) {
        platforms.remove(platform)
    }
}
