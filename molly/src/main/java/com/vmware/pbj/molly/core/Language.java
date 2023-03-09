package com.vmware.pbj.molly.core;

import java.util.*;
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
        unitsByName.putIfAbsent(unit.getName(), unit);
        unitsByName.putIfAbsent(unit.getPluralName(), unit);

        if (unit.getContext().isPresent()) {
            unitsByName.putIfAbsent(String.join(" ", unit.getContext().get(), unit.getName()), unit);
            unitsByName.putIfAbsent(String.join(" ", unit.getContext().get(), unit.getPluralName()), unit);
        }
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

    public Unit getUnitByName(String unitName) {
        return unitsByName.get(unitName);
    }

    public Descriptor getDescriptorByName(String descriptorName) {
        return descriptorsByName.get(descriptorName);
    }
}
