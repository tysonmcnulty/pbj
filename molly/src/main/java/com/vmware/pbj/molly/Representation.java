package com.vmware.pbj.molly;

import java.util.HashMap;
import java.util.Map;

public enum Representation {
    BOOLEAN("boolean"),
    DECIMAL("decimal"),
    NUMBER("number"),
    STRING("string"),
    TERM("term"),
    ;

    public final String label;

    Representation(String label) {
        this.label = label;
    }

    private static final Map<String, Representation> BY_LABEL = new HashMap<>();

    static {
        for (Representation r: values()) {
            BY_LABEL.put(r.label, r);
        }
        BY_LABEL.put("true or false", BOOLEAN);
        BY_LABEL.put("text", STRING);
    }

    public static Representation labeled(String label) {
        return BY_LABEL.getOrDefault(label, Representation.TERM);
    }
}
