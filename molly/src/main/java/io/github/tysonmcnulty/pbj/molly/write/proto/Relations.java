package io.github.tysonmcnulty.pbj.molly.write.proto;

import com.google.protobuf.DescriptorProtos;
import io.github.tysonmcnulty.pbj.molly.core.relation.Categorization;
import io.github.tysonmcnulty.pbj.molly.core.relation.Composition;
import io.github.tysonmcnulty.pbj.molly.core.relation.Description;

import static com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Label.LABEL_OPTIONAL;
import static com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED;
import static com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Type.*;
import static io.github.tysonmcnulty.pbj.molly.write.proto.Syntax.fieldNameOf;
import static io.github.tysonmcnulty.pbj.molly.write.proto.Syntax.typeNameOf;

public class Relations {
    public static void applyComposition(Composition composition, MollyProtoGenerationContext context) {
        if (composition.getOperator().isObviated()) return;

        var builder = context.getBuildersByUnitName().get(composition.getMutant().getUnitName());

        var mutation = composition.getMutation();
        var mutationRepresentation = context.getLanguage().representationOf(mutation);

        var fieldType = new Syntax.FieldTypeResolver(context.getConfig()).apply(mutationRepresentation);
        var fieldBuilder = DescriptorProtos.FieldDescriptorProto.newBuilder()
                .setType(fieldType);
        fieldBuilder.setNumber(context.yieldFieldNumber(composition.getMutant().getUnitName()));
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


    public static void applyCategorization(Categorization categorization, MollyProtoGenerationContext context) {

    }

    public static void applyDescription(Description description, MollyProtoGenerationContext context) {
        if (description.getOperator().isObviated()) return;

        var builder = context.getBuildersByUnitName().get(description.getMutant().getUnitName());
        var fieldBuilder = DescriptorProtos.FieldDescriptorProto.newBuilder()
                .setType(TYPE_BOOL)
                .setName(description.getMutation().getName())
                .setNumber(context.yieldFieldNumber(description.getMutant().getUnitName()));

        if (description.getOperator().isQualified()) {
            fieldBuilder.setLabel(LABEL_OPTIONAL);
        }

        builder.addField(fieldBuilder);
    }
}
