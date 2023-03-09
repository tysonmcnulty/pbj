package io.github.tysonmcnulty;

import java.util.Collection;

public class Bread {
    public static class Slice {}

    public static class Loaf {
        protected Collection<Slice> slices;

        public Collection<Slice> getSlices() {
            return slices;
        }
    }
}
