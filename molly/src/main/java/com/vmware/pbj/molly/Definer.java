package com.vmware.pbj.molly;

import java.util.HashMap;
import java.util.Map;

public enum Definer {
    IS_EITHER("is either"),
    IS_JUST("is just");

    public final String label;

    Definer(String label) {
        this.label = label;
    }

    private static final Map<String, Definer> BY_LABEL = new HashMap<>();

    static {
        for (Definer c: values()) {
            BY_LABEL.put(c.label, c);
        }
    }

    public static Definer valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }
}
