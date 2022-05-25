package com.vmware.pbj.feature

interface Actor {
    fun act(description: String)

    fun receive(it: Any)
}
