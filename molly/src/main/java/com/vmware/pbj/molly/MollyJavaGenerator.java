package com.vmware.pbj.molly;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Arrays;

import static org.apache.commons.lang3.StringUtils.capitalize;

public class MollyJavaGenerator {

    public static final String PACKAGE = "com.vmware.example";

    public void read(InputStream source) {

    }

    public void process() {

    }

    public void write(Path dir) {
        try {
            for (String termName: Arrays.asList("countertop", "kitchen")) {
                TypeSpec typeSpec = TypeSpec.classBuilder(capitalize(termName))
                        .addModifiers(Modifier.PUBLIC)
                        .build();
                JavaFile javaFile = JavaFile.builder(PACKAGE, typeSpec).build();
                System.out.printf("Writing %s to %s%n", capitalize(termName), dir);
                javaFile.writeToPath(dir);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
