package com.vmware.pbj.molly;

import java.util.HashMap;
import java.util.Map;

public enum Composer {
    HAS("has"),
    HAS_MANY("has many");

    public final String label;

    Composer(String label) {
        this.label = label;
    }

    private static final Map<String, Composer> BY_LABEL = new HashMap<>();

    static {
        for (Composer c: values()) {
            BY_LABEL.put(c.label, c);
        }
    }

    public static Composer labeled(String label) {
        return BY_LABEL.get(label);
    }
}
