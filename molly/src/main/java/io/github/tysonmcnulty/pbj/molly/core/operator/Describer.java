package io.github.tysonmcnulty.pbj.molly.core.operator;

import java.util.Objects;

public class Describer extends Operator {

    private final boolean isQualified;
    private final boolean isObviated;

    public Describer() {
        this(false, false);
    }

    public Describer(boolean isQualified, boolean isObviated) {
        this.isQualified = isQualified;
        this.isObviated = isObviated;
    }

    public boolean isQualified() {
        return isQualified;
    }

    public boolean isObviated() {
        return isObviated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Describer describer = (Describer) o;
        return isQualified == describer.isQualified && isObviated == describer.isObviated;
    }

    @Override
    public int hashCode() {
        return Objects.hash(isQualified, isObviated);
    }

    @Override
    public String toString() {
        return "Describer{" +
            "isQualified=" + isQualified +
            ", isObviated=" + isObviated +
            '}';
    }
}
