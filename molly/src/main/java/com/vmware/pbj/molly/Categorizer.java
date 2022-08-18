package com.vmware.pbj.molly;

import java.util.HashMap;
import java.util.Map;

public enum Categorizer {
    IS_A_KIND_OF("is a kind of"),
    IS_A_TYPE_OF("is a type of"),
    IS_JUST("is just");

    public final String label;

    Categorizer(String label) {
        this.label = label;
    }

    private static final Map<String, Categorizer> BY_LABEL = new HashMap<>();

    static {
        for (Categorizer c: values()) {
            BY_LABEL.put(c.label, c);
        }
    }

    public static Categorizer labeled(String label) {
        return BY_LABEL.get(label);
    }
}
