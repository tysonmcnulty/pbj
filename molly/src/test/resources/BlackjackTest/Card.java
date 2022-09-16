package io.github.tysonmcnulty;

public abstract class Card {
    protected Rank rank;

    protected Suit suit;

    public Rank getRank() {
        return rank;
    }

    public Suit getSuit() {
        return suit;
    }
}
