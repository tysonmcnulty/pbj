package io.github.tysonmcnulty.pbj.molly.write;

import java.io.Serializable;

public class MollyJavaGeneratorConfig implements Serializable {

    private final String javaPackage;

    public String getJavaPackage() {
        return javaPackage;
    }

    public static MollyJavaGeneratorConfig.Builder builder() {
        return new MollyJavaGeneratorConfig.Builder();
    }

    private MollyJavaGeneratorConfig(Builder builder) {
        this.javaPackage = builder.javaPackage;
    }

    public static class Builder {

        private String javaPackage = "io.github.tysonmcnulty";

        public MollyJavaGeneratorConfig build() {
            return new MollyJavaGeneratorConfig(this);
        }

        public Builder javaPackage(String javaPackage) {
            this.javaPackage = javaPackage;
            return this;
        }
    }
}
