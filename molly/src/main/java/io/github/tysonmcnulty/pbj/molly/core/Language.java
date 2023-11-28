package io.github.tysonmcnulty.pbj.molly.core;

import io.github.tysonmcnulty.pbj.molly.core.relation.Categorization;
import io.github.tysonmcnulty.pbj.molly.core.relation.Definition;
import io.github.tysonmcnulty.pbj.molly.core.relation.Relation;
import io.github.tysonmcnulty.pbj.molly.core.term.Descriptor;
import io.github.tysonmcnulty.pbj.molly.core.term.Unit;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Language {
    private final Map<String, Unit> unitsByName = new LinkedHashMap<>();
    private final Map<String, Descriptor> descriptorsByName = new LinkedHashMap<>();
    private final Set<Relation<?, ?>> relations = new LinkedHashSet<>();
    private final Map<String, Set<Unit>> childrenByUnitName = new HashMap<>();
    private final Map<String, Set<Unit>> parentsByUnitName = new HashMap<>();

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

        if (relation instanceof Categorization) {
            var categorization = (Categorization) relation;
            var parent = categorization.getMutation();
            var child = categorization.getMutant();
            this.childrenByUnitName.computeIfAbsent(parent.getUnitName(), (s) -> new HashSet<>())
                    .add(child);
            this.parentsByUnitName.computeIfAbsent(child.getUnitName(), (s) -> new HashSet<>())
                    .add(parent);
        }
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

    public Stream<Categorization> getCategorizations() {
        return getRelations().stream()
                .filter(r -> r instanceof Categorization)
                .map(r -> (Categorization) r);
    }
    public Function<String, List<Unit>> getCategorizer() {
        var categorizationsByMutantUnitName = getCategorizations()
                .collect(Collectors.toUnmodifiableMap(c -> c.getMutant().getUnitName(), c -> c));

        return (unitName) -> {
            var units = new ArrayList<Unit>();
            var unit = getUnitByName(unitName);
            units.add(getUnitByName(unitName));
            do {
                var curr = units.get(units.size() - 1);
                var currentUnitName = curr.getUnitName();
                var nextCategorization = categorizationsByMutantUnitName.getOrDefault(currentUnitName, null);
                if (nextCategorization == null) {
                    return units;
                }
                units.add(nextCategorization.getMutation());
            } while (true);
        };
    }

    public Set<Unit> getChildren(Unit unit) {
        return childrenByUnitName.computeIfAbsent(unit.getUnitName(), (s) -> new HashSet<>());
    }

    public Set<Unit> getParents(Unit unit) {
        return parentsByUnitName.computeIfAbsent(unit.getUnitName(), (s) -> new HashSet<>());
    }
}
