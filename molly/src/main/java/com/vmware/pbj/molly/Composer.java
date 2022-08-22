package com.vmware.pbj.molly;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Composer {

    private final Qualifier qualifier;
    private final Verb verb;

    public Composer(Verb verb) {
        this(verb, null);
    }

    public Composer(Verb verb, Qualifier qualifier) {
        this.verb = verb;
        this.qualifier = qualifier;
    }

    public Verb getVerb() {
        return this.verb;
    }

    public Optional<Qualifier> getQualifier() {
        return Optional.ofNullable(qualifier);
    }

    public enum Verb {
        HAS("has"),
        HAVE("have"),
        HAS_MANY("has many"),
        HAVE_MANY("have many")
        ;

        public final String label;

        Verb(String label) {
            this.label = label;
        }

        private static final Map<String, Verb> BY_LABEL = new HashMap<>();

        static {
            for (Verb c: values()) {
                BY_LABEL.put(c.label, c);
            }
        }

        public static Verb labeled(String label) {
            return BY_LABEL.get(label);
        }
    }
}

