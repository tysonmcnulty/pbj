package com.vmware.pbj.molly.core;

import com.vmware.pbj.molly.core.relation.Definition;
import com.vmware.pbj.molly.core.relation.Relation;
import com.vmware.pbj.molly.core.term.Descriptor;
import com.vmware.pbj.molly.core.term.Unit;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Language {
    Map<String, Unit> unitsByName = new LinkedHashMap<>();
    Map<String, Descriptor> descriptorsByName = new LinkedHashMap<>();
    Set<Relation<?, ?>> relations = new LinkedHashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Language language = (Language) o;
        return unitsByName.equals(language.unitsByName) && descriptorsByName.equals(language.descriptorsByName) && relations.equals(language.relations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(unitsByName, descriptorsByName, relations);
    }

    @Override
    public String toString() {
        return "Language{" +
            "unitsByName=" + unitsByName +
            ", descriptorsByName=" + descriptorsByName +
            ", relations=" + relations +
            '}';
    }

    public void addUnit(Unit unit) {
        if (Unit.primitives.containsValue(unit)) return;

        unitsByName.putIfAbsent(unit.getName(), unit);
        unitsByName.putIfAbsent(unit.getPluralName(), unit);
        unitsByName.putIfAbsent(unit.getUnitName(), unit);
        unitsByName.putIfAbsent(unit.getPluralUnitName(), unit);
    }

    public void addDescriptor(Descriptor descriptor) {
        descriptorsByName.putIfAbsent(descriptor.getName(), descriptor);
    }

    public void addRelation(Relation<?, ?> relation) {
        this.relations.add(relation);
    }

    public Stream<Unit> getUnits() {
        return unitsByName.values().stream().distinct();
    }

    public Set<Relation<?, ?>> getRelations() {
        return this.relations;
    }

    public Unit representationOf(Unit unit) {
        var definitions = getRelations().stream()
            .filter(r -> r instanceof Definition)
            .collect(Collectors.toMap(r -> r.getMutant().getUnitName(), r -> (Definition) r));
        var definitionMutants = definitions.values().stream()
            .map(Relation::getMutant)
            .collect(Collectors.toSet());
        var representation = unit;

        while (definitionMutants.contains(representation)) {
            representation = definitions.get(representation.getUnitName()).getMutation();
        }

        return representation;
    }

    public Unit getUnitByName(String unitName) {
        if (Unit.primitives.containsKey(unitName)) {
            return Unit.primitives.get(unitName);
        }

        return unitsByName.get(unitName);
    }

    public Descriptor getDescriptorByName(String descriptorName) {
        return descriptorsByName.get(descriptorName);
    }
}
