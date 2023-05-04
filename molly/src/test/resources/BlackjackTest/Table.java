package io.github.tysonmcnulty;

import java.util.Collection;

public class Table {
    protected Collection<Player> players;

    protected Dealer dealer;

    protected Shoe shoe;

    public Collection<Player> getPlayers() {
        return players;
    }

    public Dealer getDealer() {
        return dealer;
    }

    public Shoe getShoe() {
        return shoe;
    }
}
