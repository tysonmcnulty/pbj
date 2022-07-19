package com.vmware.pbj.molly;

public class MollyJavaGeneratorConfig {

    private final String javaPackage;

    private MollyJavaGeneratorConfig(Builder builder) {
        this.javaPackage = builder.javaPackage;
    }

    public static MollyJavaGeneratorConfig.Builder builder() {
        return new MollyJavaGeneratorConfig.Builder();
    }

    public String getJavaPackage() {
        return javaPackage;
    }

    public static class Builder {

        private String javaPackage = "com.vmware.example";

        public MollyJavaGeneratorConfig build() {
            return new MollyJavaGeneratorConfig(this);
        }

        public Builder javaPackage(String javaPackage) {
            this.javaPackage = javaPackage;
            return this;
        }
    }
}
