package com.vmware.example;

import java.util.Collection;

public abstract class Shoe {
    protected Collection<Card> cards;

    public Collection<Card> getCards() {
        return cards;
    }
}