package com.vmware.example;

import java.util.Collection;

public abstract class Player {
    protected Collection<Chip> chips;

    public Collection<Chip> getChips() {
        return chips;
    }
}
