package com.vmware.pbj.molly;

import java.util.HashMap;
import java.util.Map;

public enum Describer {
    IS_EVIDENTLY("is evidently");

    public final String label;

    Describer(String label) {
        this.label = label;
    }

    private static final Map<String, Describer> BY_LABEL = new HashMap<>();

    static {
        for (Describer c: values()) {
            BY_LABEL.put(c.label, c);
        }
    }

    public static Describer valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }
}
