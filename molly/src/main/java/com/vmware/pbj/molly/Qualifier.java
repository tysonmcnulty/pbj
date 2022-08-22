package com.vmware.pbj.molly;

import java.util.HashMap;
import java.util.Map;

public enum Qualifier {
    MAY("may"),
    PROBABLY("probably"),
    ;

    public final String label;

    Qualifier(String label) {
        this.label = label;
    }

    private static final Map<String, Qualifier> BY_LABEL = new HashMap<>();

    static {
        for (Qualifier c: values()) {
            BY_LABEL.put(c.label, c);
        }
    }

    public static Qualifier labeled(String label) {
        return BY_LABEL.get(label);
    }
}
