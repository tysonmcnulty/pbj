package io.github.tysonmcnulty;

import java.util.Collection;
import java.util.Optional;

public abstract class Player {
    protected Collection<Chip> chips;

    protected Hand hand;

    public Collection<Chip> getChips() {
        return chips;
    }

    public Optional<Hand> getHand() {
        return Optional.ofNullable(hand);
    }
}
