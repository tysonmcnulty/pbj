package com.vmware.pbj.molly;

import java.util.HashMap;
import java.util.Map;

public enum Subordinator {
    WHEREIN("wherein"),
    WHICH("which"),
    ;

    public final String label;

    Subordinator(String label) {
        this.label = label;
    }

    private static final Map<String, Subordinator> BY_LABEL = new HashMap<>();

    static {
        for (Subordinator c: values()) {
            BY_LABEL.put(c.label, c);
        }
    }

    public static Subordinator labeled(String label) {
        return BY_LABEL.get(label);
    }
}
