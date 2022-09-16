package io.github.tysonmcnulty;

import java.util.Collection;

public abstract class Deck {
    protected Collection<Card> cards;

    public Collection<Card> getCards() {
        return cards;
    }
}
