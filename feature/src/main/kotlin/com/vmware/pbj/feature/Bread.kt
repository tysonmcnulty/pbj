package com.vmware.pbj.feature

class Bread(
    var sealed: Boolean = false
): Platform {
    override fun actions(): Map<String, () -> Unit> {
        return mapOf(
            "unseal bread" to this::unseal,
            "seal bread" to this::seal
        )
    }

    private fun seal() {
        this.sealed = true
    }

    private fun unseal() {
        this.sealed = false
    }
}
