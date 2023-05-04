package com.vmware.pbj.molly.write;

import com.squareup.javapoet.*;
import com.vmware.pbj.molly.core.Language;
import com.vmware.pbj.molly.core.relation.Categorization;
import com.vmware.pbj.molly.core.relation.Composition;
import com.vmware.pbj.molly.core.relation.Description;

import javax.lang.model.element.Modifier;

import static com.vmware.pbj.molly.write.Syntax.classNameOf;
import static com.vmware.pbj.molly.write.Syntax.fieldNameOf;
import static org.apache.commons.text.WordUtils.capitalize;

public class Relations {

    public static void applyComposition(Composition composition, TypeSpec.Builder builder, Language language, MollyJavaGeneratorConfig config) {
        var mutation = composition.getMutation();
        var mutationRepresentation = language.representationOf(mutation);
        var mutationTypeName = new Syntax.TypeNameResolver(config)
            .apply(mutationRepresentation);

        String fieldName;
        TypeName fieldType;

        if (composition.isCategorical()) {
            var typeVariable = TypeVariableName.get("T").withBounds(mutationTypeName);
            builder.addTypeVariable(typeVariable);
            fieldType = typeVariable;
        } else {
            fieldType = mutationTypeName;
        }

        switch (composition.getCardinality()) {
        case ONE_TO_MANY:
            fieldName = fieldNameOf(mutation.getPluralName());
            fieldType = ParameterizedTypeName.get(
                ClassName.get("java.util", "Collection"),
                fieldType);
            break;
        case ONE_TO_ONE:
        default:
            fieldName = fieldNameOf(mutation.getName());
            break;
        }

        var accessorName = "get" + capitalize(fieldName);
        String accessorStatement;
        TypeName accessorReturnType;

        if (composition.getOperator().isQualified()) {
            accessorStatement = String.format("return Optional.ofNullable(%s)", fieldName);
            accessorReturnType = ParameterizedTypeName.get(
                ClassName.get("java.util", "Optional"),
                fieldType);
        } else {
            accessorReturnType = fieldType;
            accessorStatement = String.format("return %s", fieldName);
        }

        var field = FieldSpec.builder(
            fieldType,
            fieldName,
            Modifier.PROTECTED
        );

        var accessor = MethodSpec.methodBuilder(accessorName)
            .addModifiers(Modifier.PUBLIC)
            .returns(accessorReturnType);

        if (composition.getOperator().isObviated()) {
            builder.addModifiers(Modifier.ABSTRACT);
            builder
                .addMethod(accessor.addModifiers(Modifier.ABSTRACT).build());
        } else {
            builder
                .addField(field.build())
                .addMethod(accessor.addStatement(accessorStatement).build());
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

        var fieldName = "is" + capitalize(fieldNameOf(mutation.getName()));
        var fieldType = TypeName.BOOLEAN;
        var accessorName = fieldName;

        var field = FieldSpec.builder(
            fieldType,
            fieldName,
            Modifier.PROTECTED
        );

        String accessorStatement;
        TypeName accessorReturnType;

        if (description.getOperator().isQualified()) {
            accessorStatement = String.format("return Optional.ofNullable(%s)", fieldName);
            accessorReturnType = ParameterizedTypeName.get(
                ClassName.get("java.util", "Optional"),
                fieldType);
        } else {
            accessorReturnType = fieldType;
            accessorStatement = String.format("return %s", fieldName);
        }

        var accessor = MethodSpec.methodBuilder(accessorName)
            .addModifiers(Modifier.PUBLIC)
            .returns(accessorReturnType);

        if (description.getOperator().isObviated()) {
            builder.addModifiers(Modifier.ABSTRACT);
            builder
                .addMethod(accessor.addModifiers(Modifier.ABSTRACT).build());
        } else {
            builder
                .addField(field.build())
                .addMethod(accessor.addStatement(accessorStatement).build());
        }

        description.getMutation().getNegation().ifPresent((negation) -> {
            var negationAccessorName = "is" + capitalize(fieldNameOf(negation));

            String negationAccessorStatement;
            TypeName negationAccessorReturnType;

            if (description.getOperator().isQualified()) {
                negationAccessorStatement = String.format(
                    "return Optional.ofNullable(this.%s().isPresent() ? !this.%s().get() : null)",
                    fieldName,
                    fieldName
                );
                negationAccessorReturnType = ParameterizedTypeName.get(
                    ClassName.get("java.util", "Optional"),
                    fieldType);
            } else {
                negationAccessorStatement = String.format("return !this.%s()", accessorName);
                negationAccessorReturnType = fieldType;
            }

            builder.addMethod(
                MethodSpec.methodBuilder(negationAccessorName)
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement(negationAccessorStatement)
                    .returns(negationAccessorReturnType)
                    .build());
        });
    }
}
