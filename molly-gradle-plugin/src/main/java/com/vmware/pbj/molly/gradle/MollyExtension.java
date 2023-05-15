package com.vmware.pbj.molly.gradle;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;

public abstract class MollyExtension {

    public abstract Property<String> getJavaPackage();

    public abstract RegularFileProperty getInputFile();

    public abstract DirectoryProperty getOutputDir();

    public abstract Property<String> getSourceSetName();
}
