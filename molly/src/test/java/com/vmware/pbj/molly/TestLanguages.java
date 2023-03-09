package com.vmware.pbj.molly;

import com.vmware.pbj.molly.core.*;

import java.util.List;

public class TestLanguages {

    private static Language kitchen() {
        var language = new Language();

        var bread = Unit.fromInflectedName("bread");
        var countertop = Unit.fromInflectedName("countertop");
        var food = Unit.fromInflectedName("food");
        var kitchen = Unit.fromInflectedName("kitchen");
        var pantry = Unit.fromInflectedName("pantry");
        var sealed = new Descriptor("sealed", "unsealed");

        var units = List.of(
            bread,
            countertop,
            food,
            kitchen,
            pantry
        );
        List<Descriptor> descriptors = List.of(
            sealed
        );
        var relations = List.of(
            new Composition(kitchen, countertop),
            new Composition.Builder(pantry, food).cardinality(Cardinality.ONE_TO_MANY).build(),
            new Categorization(bread, food),
            new Description(bread, sealed)
        );

        units.forEach(language::addUnit);
        descriptors.forEach(language::addDescriptor);
        relations.forEach(language::addRelation);

        return language;
    }

    private static Language pbj() {
        var language = new Language();

        var pbj = Unit.fromInflectedName("PBJ");
        var slice = Unit.fromInflectedName("slice");
        var loaf = Unit.fromInflectedName("loaf");
        var jelly = Unit.fromInflectedName("jelly");
        var jar = Unit.fromInflectedName("jar");
        var peanutButter = Unit.fromInflectedName("peanut butter");
        var spread = Unit.fromInflectedName("spread");
        loaf.setContext("bread");
        slice.setContext("bread");

        var units = List.of(
            pbj,
            slice,
            peanutButter,
            jelly,
            jar,
            spread,
            loaf
        );

        var relations = List.of(
            new Composition.Builder(pbj, slice).cardinality(Cardinality.ONE_TO_MANY).build(),
            new Composition(pbj, peanutButter),
            new Composition(pbj, jelly),
            new Composition.Builder(jar, spread).categorical(true).build(),
            new Categorization(peanutButter, spread),
            new Categorization(jelly, spread),
            new Composition.Builder(loaf, slice).cardinality(Cardinality.ONE_TO_MANY).build()
        );

        units.forEach(language::addUnit);
        relations.forEach(language::addRelation);

        return language;
    }

    public static Language get(String languageName) {
        if (languageName.equalsIgnoreCase("kitchen")) return kitchen();
        if (languageName.equalsIgnoreCase("pbj")) return pbj();
        return null;
    }
}
