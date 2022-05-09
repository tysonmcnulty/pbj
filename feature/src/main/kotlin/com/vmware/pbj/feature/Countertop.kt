package com.vmware.pbj.feature

class Countertop(
    var bread: Bread? = null
) {
    fun receive(bread: Bread) {
        this.bread = bread
    }
}
