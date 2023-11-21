package io.github.tysonmcnulty.pbj.molly.write.proto;

import com.google.protobuf.DescriptorProtos;
import io.github.tysonmcnulty.pbj.molly.core.term.Enumeration;
import io.github.tysonmcnulty.pbj.molly.core.term.Unit;
import org.apache.commons.text.WordUtils;

import java.util.function.Function;

public class Syntax {
    public static String messageNameOf(String termName) {
        if (termName.matches("[A-Z]+")) return termName;
        return WordUtils.capitalizeFully(termName).replaceAll("\\W", "");
    }

    public static String fieldNameOf(String termName) {
        return WordUtils.uncapitalize(messageNameOf(termName));
    }

    public static String typeNameOf(Unit unit) {
        return unit.getContext().isPresent()
                ? messageNameOf(unit.getContext().get()) + "." + messageNameOf(unit.getName())
                : messageNameOf(unit.getName());
    }

    public static class FieldTypeResolver implements Function<Unit, DescriptorProtos.FieldDescriptorProto.Type> {

        private final MollyProtoGeneratorConfig config;

        public FieldTypeResolver(MollyProtoGeneratorConfig config) {
            this.config = config;
        }

        @Override
        public DescriptorProtos.FieldDescriptorProto.Type apply(Unit unit) {
            switch (unit.getName()) {
                case "string":
                    return DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING;
                case "number":
                    return DescriptorProtos.FieldDescriptorProto.Type.TYPE_SINT32;
                case "decimal":
                    return DescriptorProtos.FieldDescriptorProto.Type.TYPE_DOUBLE;
                case "boolean":
                    return DescriptorProtos.FieldDescriptorProto.Type.TYPE_BOOL;
                default:
                    return (unit instanceof Enumeration)
                            ? DescriptorProtos.FieldDescriptorProto.Type.TYPE_ENUM
                            : DescriptorProtos.FieldDescriptorProto.Type.TYPE_MESSAGE;
            }
        }
    }
}
