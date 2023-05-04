package io.github.tysonmcnulty;

import java.util.Optional;

public class Dealer {
    protected Hand hand;

    public Optional<Hand> getHand() {
        return Optional.ofNullable(hand);
    }
}
