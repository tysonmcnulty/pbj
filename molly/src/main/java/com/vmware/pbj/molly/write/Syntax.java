package com.vmware.pbj.molly.write;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.vmware.pbj.molly.core.Term;
import com.vmware.pbj.molly.core.Unit;
import org.apache.commons.text.WordUtils;

import java.util.Map;
import java.util.function.Function;

public class Syntax {
    public static String classNameOf(String termName) {
        if (termName.matches("[A-Z]+")) return termName;
        return WordUtils.capitalizeFully(termName).replaceAll("\\W", "");
    }

    public static String fieldNameOf(String termName) {
        return WordUtils.uncapitalize(classNameOf(termName));
    }

    public static class TypeNameResolver implements Function<Unit, TypeName> {

        private final Map<String, TypeSpec.Builder> buildersByName;
        private final MollyJavaGeneratorConfig config;

        public TypeNameResolver(Map<String, TypeSpec.Builder> buildersByName, MollyJavaGeneratorConfig config) {
            this.buildersByName = buildersByName;
            this.config = config;
        }

        @Override
        public TypeName apply(Unit unit) {
            var representation = resolveRepresentation(unit);
            switch (representation) {
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
                        return ClassName.get(config.getJavaPackage(), classNameOf(unit.getContext().get()), classNameOf(representation));
                    } else {
                        return ClassName.get(config.getJavaPackage(), classNameOf(representation));
                    }
            }
        }

        private String resolveRepresentation(Term term) {
            return term.getName();
        }
    }
}
