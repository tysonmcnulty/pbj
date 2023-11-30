package io.github.tysonmcnulty.pbj.molly.write.java;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import io.github.tysonmcnulty.pbj.molly.core.term.Enumeration;
import io.github.tysonmcnulty.pbj.molly.core.term.Unit;
import org.apache.commons.text.WordUtils;

import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public class Syntax {
    public static String classNameOf(String termName) {
        if (termName.matches("[A-Z]+")) return termName;
        return WordUtils.capitalizeFully(termName).replaceAll("\\W", "");
    }

    public static List<String> enumValuesOf(Enumeration enumeration) {
        return enumeration.getValues().stream()
                .map((value) -> enumeration.getName().toUpperCase().replaceAll("\\W", "_")
                    + "_" + value.toUpperCase())
                .collect(toList());
    }

    public static String fieldNameOf(String termName) {
        return WordUtils.uncapitalize(classNameOf(termName));
    }

    public static class TypeNameResolver implements Function<Unit, TypeName> {

        private final MollyJavaGeneratorConfig config;

        public TypeNameResolver(MollyJavaGeneratorConfig config) {
            this.config = config;
        }

        @Override
        public TypeName apply(Unit unit) {
            switch (unit.getName()) {
                case "string":
                    return TypeName.get(String.class);
                case "number":
                    return TypeName.get(int.class);
                case "decimal":
                    return TypeName.get(double.class);
                case "boolean":
                    return TypeName.get(boolean.class);
                default:
                    if (unit.getContext().isPresent()) {
                        return ClassName.get(
                            config.getJavaPackage(),
                            classNameOf(unit.getContext().get()),
                            classNameOf(unit.getName()));
                    } else {
                        return ClassName.get(
                            config.getJavaPackage(),
                            classNameOf(unit.getName()));
                    }
            }
        }
    }
}
