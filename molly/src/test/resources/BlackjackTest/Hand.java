package com.vmware.example;

import java.util.Collection;

public abstract class Hand {
    protected Collection<Card> cards;

    public Collection<Card> getCards() {
        return cards;
    }

    public abstract int getValue();

    public abstract boolean isSoft();
}
