package com.vmware.pbj.feature

interface Platform {
    fun actions(): Map<String, () -> Unit>
}
