package io.github.tysonmcnulty.pbj.molly.write.proto;

import com.google.protobuf.DescriptorProtos;
import io.github.tysonmcnulty.pbj.molly.core.relation.Categorization;
import io.github.tysonmcnulty.pbj.molly.core.relation.Composition;
import io.github.tysonmcnulty.pbj.molly.core.relation.Definition;
import io.github.tysonmcnulty.pbj.molly.core.relation.Description;
import io.github.tysonmcnulty.pbj.molly.core.term.Unit;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Label.LABEL_OPTIONAL;
import static com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED;
import static com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Type.*;
import static io.github.tysonmcnulty.pbj.molly.write.proto.Syntax.fieldNameOf;
import static io.github.tysonmcnulty.pbj.molly.write.proto.Syntax.typeNameOf;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;

public class Relations {
    public static void applyComposition(Composition composition, MollyProtoGenerationContext context) {
        if (composition.getOperator().isObviated()) return;

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

        Map<String, Definition> definitionsByContext;
        if (composition.isCategorical()) {
            definitionsByContext = context.getLanguage().getRelations().stream()
                    .filter(r -> r instanceof Definition)
                    .filter(r -> r.getMutant().getContext().isPresent())
                    .filter(r -> r.getMutant().getName().equals(composition.getMutation().getName()))
                    .collect(Collectors.toMap(r -> r.getMutant().getContext().get(), r -> (Definition) r));
        } else {
            definitionsByContext = emptyMap();
        }

        var mutant = composition.getMutant();
        Deque<Unit> remainingUnits = new ArrayDeque<>(singletonList(mutant));
        Map<String, Unit> mutationsByContext = new HashMap<>();
        while (!remainingUnits.isEmpty()) {
            var next = remainingUnits.pop();
            if (definitionsByContext.containsKey(next.getName())) {
                mutationsByContext.put(next.getName(), definitionsByContext.get(next.getName()).getMutation());
            }
            var children = context.getLanguage().getChildren(next);
            var nextBuilder = context.getBuildersByUnitName().get(next.getUnitName());
            DescriptorProtos.FieldDescriptorProto.Builder nextFieldBuilder;
            if (mutationsByContext.containsKey(next.getName())) {
                var scopedMutation = mutationsByContext.get(next.getName());
                children.forEach(child -> mutationsByContext.put(child.getName(), scopedMutation));
                var scopedMutationRepresentation = context.getLanguage().representationOf(scopedMutation);
                nextFieldBuilder = DescriptorProtos.FieldDescriptorProto.newBuilder().mergeFrom(fieldBuilder.build())
                        .setTypeName(typeNameOf(scopedMutationRepresentation));
            } else {
                nextFieldBuilder = fieldBuilder;
            }

            nextBuilder.addField(nextFieldBuilder);
            remainingUnits.addAll(children);
        }
    }


    public static void applyCategorization(Categorization categorization, MollyProtoGenerationContext context) {

    }

    public static void applyDescription(Description description, MollyProtoGenerationContext context) {
        if (description.getOperator().isObviated()) return;

        var builder = context.getBuildersByUnitName().get(description.getMutant().getUnitName());
        var fieldBuilder = DescriptorProtos.FieldDescriptorProto.newBuilder()
                .setType(TYPE_BOOL)
                .setName(fieldNameOf(description.getMutation().getName()))
                .setNumber(context.yieldFieldNumber(description.getMutant().getUnitName()));

        if (description.getOperator().isQualified()) {
            fieldBuilder.setLabel(LABEL_OPTIONAL);
        }

        Deque<Unit> children = new ArrayDeque<>(singletonList(description.getMutant()));
        while (!children.isEmpty()) {
            var next = children.pop();
            children.addAll(context.getLanguage().getChildren(next));
            var nextBuilder = context.getBuildersByUnitName().get(next.getUnitName());
            nextBuilder.addField(fieldBuilder);
        }
    }
}
