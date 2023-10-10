package io.github.tysonmcnulty;

public class Jar<SpreadType extends Spread> {
    protected SpreadType spread;

    public SpreadType getSpread() {
        return spread;
    }
}
