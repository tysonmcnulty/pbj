package com.vmware.example;

import java.util.Optional;

public abstract class Dealer {
    protected Hand hand;

    public Optional<Hand> getHand() {
        return Optional.ofNullable(hand);
    }
}
