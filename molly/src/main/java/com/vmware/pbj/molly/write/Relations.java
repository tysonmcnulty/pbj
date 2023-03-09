package com.vmware.pbj.molly.write;

import com.squareup.javapoet.*;
import com.vmware.pbj.molly.core.Categorization;
import com.vmware.pbj.molly.core.Composition;
import com.vmware.pbj.molly.core.Description;

import javax.lang.model.element.Modifier;

import static com.vmware.pbj.molly.write.Syntax.classNameOf;
import static com.vmware.pbj.molly.write.Syntax.fieldNameOf;

public class Relations {

    public static void applyComposition(Composition composition, TypeSpec.Builder builder, Syntax.TypeNameResolver typeNameResolver) {
        var mutation = composition.getMutation();
        var mutationTypeName = typeNameResolver.apply(mutation);
        var mutationFieldName = fieldNameOf(mutation.getName());
        var mutationClassName = classNameOf(mutation.getName());

        FieldSpec mutationFieldSpec;

        if (composition.isCategorical()) {
            var typeVariable = TypeVariableName.get("T").withBounds(mutationTypeName);
            builder.addTypeVariable(typeVariable);

            mutationFieldSpec = FieldSpec.builder(
                typeVariable,
                mutationFieldName,
                Modifier.PROTECTED
            ).build();
        } else {
            mutationFieldSpec = FieldSpec.builder(
                mutationTypeName,
                mutationFieldName,
                Modifier.PROTECTED
            ).build();
        }

        switch (composition.getCardinality()) {
        case ONE_TO_ONE:
            builder.addField(mutationFieldSpec);

            var accessor = MethodSpec.methodBuilder("get" + mutationClassName)
                .addModifiers(Modifier.PUBLIC);

            if (composition.getOperand().isQualified()) {
                TypeName optionalType = ParameterizedTypeName.get(
                    ClassName.get("java.util", "Optional"),
                    mutationFieldSpec.type);
                accessor
                    .addStatement(String.format("return Optional.ofNullable(%s)", mutationFieldName))
                    .returns(optionalType);
            } else {
                accessor
                    .addStatement(String.format("return %s", mutationFieldName))
                    .returns(mutationFieldSpec.type);
            }

            builder.addMethod(accessor.build());
            break;
        case ONE_TO_MANY:
            var pluralMutationName = mutation.getPluralName();
            TypeName collectionType = ParameterizedTypeName.get(
                ClassName.get("java.util", "Collection"),
                mutationFieldSpec.type);
            builder
                .addField(
                    collectionType,
                    pluralMutationName,
                    Modifier.PROTECTED)
                .addMethod(
                    MethodSpec.methodBuilder("get" + classNameOf(pluralMutationName))
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement(String.format("return %s", pluralMutationName))
                        .returns(collectionType)
                        .build());
                break;
        }
    }

    public static void applyCategorization(Categorization categorization, TypeSpec.Builder builder, MollyJavaGeneratorConfig config) {
        var mutation = categorization.getMutation();
        var mutationName = mutation.getName();
        var mutationClassName = classNameOf(mutationName);

        var supertype = ClassName.get(config.getJavaPackage(), mutationClassName);
        builder.superclass(supertype);
    }

    public static void applyDescription(Description description, TypeSpec.Builder builder) {
        var mutation = description.getMutation();
        var mutationFieldName = "is" + classNameOf(mutation.getName());

        builder.addField(TypeName.BOOLEAN, mutationFieldName, Modifier.PROTECTED);

        builder
            .addMethod(MethodSpec
                .methodBuilder(mutationFieldName)
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.BOOLEAN)
                .addStatement(String.format("return %s", mutationFieldName))
                .build()
            );
    }
}
