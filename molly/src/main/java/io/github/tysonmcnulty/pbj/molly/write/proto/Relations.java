package io.github.tysonmcnulty.pbj.molly.write.proto;

import com.google.protobuf.DescriptorProtos;
import io.github.tysonmcnulty.pbj.molly.core.Language;
import io.github.tysonmcnulty.pbj.molly.core.relation.Categorization;
import io.github.tysonmcnulty.pbj.molly.core.relation.Composition;
import io.github.tysonmcnulty.pbj.molly.core.relation.Description;

import static com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Label.LABEL_OPTIONAL;
import static com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED;
import static com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Type.*;
import static io.github.tysonmcnulty.pbj.molly.write.proto.Syntax.fieldNameOf;
import static io.github.tysonmcnulty.pbj.molly.write.proto.Syntax.typeNameOf;

public class Relations {
    public static void applyComposition(Composition composition, DescriptorProtos.DescriptorProto.Builder builder, Language language, MollyProtoGeneratorConfig config) {
        if (composition.getOperator().isObviated()) return;

        var mutation = composition.getMutation();
        var mutationRepresentation = language.representationOf(mutation);

        var fieldType = new Syntax.FieldTypeResolver(config).apply(mutationRepresentation);
        var fieldBuilder = DescriptorProtos.FieldDescriptorProto.newBuilder()
                .setType(fieldType);
        if (fieldType.equals(TYPE_ENUM) || fieldType.equals(TYPE_MESSAGE)) {
            fieldBuilder.setTypeName(typeNameOf(mutationRepresentation));
        }

        switch(composition.getCardinality()) {
            case ONE_TO_MANY:
                fieldBuilder.setLabel(LABEL_REPEATED);
                fieldBuilder.setName(fieldNameOf(mutation.getPluralName()));
                break;
            case ONE_TO_ONE:
            default:
                fieldBuilder.setName(fieldNameOf(mutation.getName()));
                break;
        }

        if (composition.getOperator().isQualified()) {
            fieldBuilder.setLabel(LABEL_OPTIONAL);
        }

        builder.addField(fieldBuilder);
    }


    public static void applyCategorization(Categorization relation, DescriptorProtos.DescriptorProto.Builder builder, Language language, MollyProtoGeneratorConfig config) {

    }

    public static void applyDescription(Description relation, DescriptorProtos.DescriptorProto.Builder builder) {
        if (relation.getOperator().isObviated()) return;

        var fieldBuilder = DescriptorProtos.FieldDescriptorProto.newBuilder()
                .setType(TYPE_BOOL)
                .setName(relation.getMutation().getName());

        if (relation.getOperator().isQualified()) {
            fieldBuilder.setLabel(LABEL_OPTIONAL);
        }

        builder.addField(fieldBuilder);
    }
}
