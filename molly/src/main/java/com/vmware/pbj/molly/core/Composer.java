package com.vmware.pbj.molly.core;

import java.util.Objects;

public class Composer extends Operand {

    private final boolean isQualified;
    private final boolean isObviated;

    public Composer(
            boolean isQualified,
            boolean isObviated
    ) {
        this.isQualified = isQualified;
        this.isObviated = isObviated;
    }

    public Composer() {
        this(false, false);
    }

    public boolean isObviated() {
        return isObviated;
    }

    public boolean isQualified() {
        return isQualified;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Composer composer = (Composer) o;
        return isQualified == composer.isQualified && isObviated == composer.isObviated;
    }

    @Override
    public int hashCode() {
        return Objects.hash(isQualified, isObviated);
    }

    @Override
    public String toString() {
        return "Composer{" +
            "isQualified=" + isQualified +
            ", isObviated=" + isObviated +
            '}';
    }
}
