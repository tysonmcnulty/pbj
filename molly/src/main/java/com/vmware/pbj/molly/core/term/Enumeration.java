package com.vmware.pbj.molly.core.term;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Enumeration extends Unit {

    private final List<String> values;

    public Enumeration(String name, String... values) {
        super(name);
        this.values = List.of(values);
    }

    public List<String> getValues() {
        return values;
    }

    public static class Builder {

        private final String name;
        private final List<String> values = new ArrayList<>();

        public Builder(String name) {
            this.name = name;
        }

        public Builder values(String... values) {
            this.values.addAll(List.of(values));
            return this;
        }

        public Enumeration build() {
            return new Enumeration(name, values.toArray(new String[0]));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Enumeration that = (Enumeration) o;
        return values.equals(that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), values);
    }

    @Override
    public String toString() {
        return "Enumeration{" +
            "name='" + name + '\'' +
            ", values=" + values +
            '}';
    }
}
