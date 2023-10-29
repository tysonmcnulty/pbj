package io.github.tysonmcnulty.pbj.molly.write.proto;

import java.io.Serializable;

public class MollyProtoGeneratorConfig implements Serializable {

    private final String javaPackage;

    public String getJavaPackage() {
        return javaPackage;
    }

    public static MollyProtoGeneratorConfig.Builder builder() {
        return new MollyProtoGeneratorConfig.Builder();
    }

    private MollyProtoGeneratorConfig(Builder builder) {
        this.javaPackage = builder.javaPackage;
    }

    public static class Builder {

        private String javaPackage = "io.github.tysonmcnulty";

        public MollyProtoGeneratorConfig build() {
            return new MollyProtoGeneratorConfig(this);
        }

        public Builder javaPackage(String javaPackage) {
            this.javaPackage = javaPackage;
            return this;
        }
    }
}
