package com.vmware.pbj.molly.gradle;

import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;

abstract public class MollyExtension {

    abstract public Property<String> getJavaPackage();

    abstract public RegularFileProperty getInputFile();

    public abstract RegularFileProperty getOutputDir();

    public abstract Property<String> getSourceSetName();
}
