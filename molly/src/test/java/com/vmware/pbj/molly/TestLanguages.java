package com.vmware.pbj.molly;

import com.vmware.pbj.molly.core.*;
import com.vmware.pbj.molly.core.operator.Composer;
import com.vmware.pbj.molly.core.operator.Describer;
import com.vmware.pbj.molly.core.relation.Categorization;
import com.vmware.pbj.molly.core.relation.Composition;
import com.vmware.pbj.molly.core.relation.Definition;
import com.vmware.pbj.molly.core.relation.Description;
import com.vmware.pbj.molly.core.term.Descriptor;
import com.vmware.pbj.molly.core.term.Enumeration;
import com.vmware.pbj.molly.core.term.Unit;

import java.util.List;

import static com.vmware.pbj.molly.core.relation.Cardinality.ONE_TO_MANY;

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
            new Composition.Builder(pantry, food).cardinality(ONE_TO_MANY).build(),
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
            new Composition.Builder(pbj, slice).cardinality(ONE_TO_MANY).build(),
            new Composition(pbj, peanutButter),
            new Composition(pbj, jelly),
            new Composition.Builder(jar, spread).categorical(true).build(),
            new Categorization(peanutButter, spread),
            new Categorization(jelly, spread),
            new Composition.Builder(loaf, slice).cardinality(ONE_TO_MANY).build()
        );

        units.forEach(language::addUnit);
        relations.forEach(language::addRelation);

        return language;
    }

    public static Language blackjack() {
        var language = new Language();

        var deck = new Unit("deck");
        var card = new Unit("card");
        var shoe = new Unit("shoe");
        var rank = new Unit("rank");
        var rankValue = new Enumeration.Builder("rank value")
            .values("ace", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "jack", "queen", "king")
            .build();
        var suit = new Unit("suit");
        var suitValue = new Enumeration.Builder("suit value")
            .values("club", "diamond", "heart", "spade")
            .build();
        var hand = new Unit("hand");
        var player = new Unit("player");
        var chip = new Unit("chip");
        var value = new Unit("value");
        var dealer = new Unit("dealer");
        var table = new Unit("table");

        var soft = new Descriptor("soft", "hard");

        var units = List.of(
            deck,
            card,
            shoe,
            rank,
            rankValue,
            suit,
            suitValue,
            hand,
            value,
            player,
            chip,
            dealer,
            table
        );

        var descriptors = List.of(
            soft
        );

        var relations = List.of(
            new Composition.Builder(deck, card)
                .cardinality(ONE_TO_MANY)
                .build(),
            new Composition.Builder(shoe, card)
                .cardinality(ONE_TO_MANY)
                .build(),
            new Composition(card, rank),
            new Definition(rank, rankValue),
            new Composition(card, suit),
            new Definition(suit, suitValue),
            new Composition.Builder(hand, card)
                .cardinality(ONE_TO_MANY)
                .build(),
            new Composition.Builder(hand, value)
                .composer(new Composer(false, true))
                .build(),
            new Definition(value, Unit.primitives.get("number")),
            new Description(hand, new Describer(false, true), soft),
            new Composition.Builder(player, chip)
                .cardinality(ONE_TO_MANY)
                .build(),
            new Composition.Builder(player, hand)
                .composer(new Composer(true, false))
                .build(),
            new Composition(chip, value),
            new Composition.Builder(dealer, hand)
                .composer(new Composer(true, false))
                .build(),
            new Composition.Builder(table, player)
                .cardinality(ONE_TO_MANY)
                .build(),
            new Composition(table, dealer),
            new Composition(table, shoe)
        );

        units.forEach(language::addUnit);
        descriptors.forEach(language::addDescriptor);
        relations.forEach(language::addRelation);

        return language;
    }

    public static Language molly() {
        var molly = new Language();

        var language = new Unit("language");
        var term = new Unit("term");
        var descriptor = new Unit("descriptor");
        var relation = new Unit("relation");
        var name = new Unit("name");
        var unit = new Unit("unit");
        var context = new Unit("context");
        var enumeration = new Unit("enumeration");
        var value = new Unit("value");
        var negation = new Unit("negation");
        var mutant = new Unit("mutant");
        var mutation = new Unit("mutation");
        var operator = new Unit("operator");

        var primitive = new Descriptor("primitive");

        var units = List.of(
            language,
            term,
            descriptor,
            relation,
            name,
            unit,
            context,
            enumeration,
            value,
            negation,
            mutant,
            mutation,
            operator
        );

        var descriptors = List.of(
            primitive
        );

        var relations = List.of(
            new Composition.Builder(language, term)
                .cardinality(ONE_TO_MANY)
                .build(),
            new Composition.Builder(language, descriptor)
                .cardinality(ONE_TO_MANY)
                .build(),
            new Composition.Builder(language, relation)
                .cardinality(ONE_TO_MANY)
                .build(),
            new Composition(term, name),
            new Definition(name, Unit.primitives.get("string")),
            new Categorization(unit, term),
            new Description(unit, new Describer(false, true), primitive),
            new Composition.Builder(unit, context)
                .composer(new Composer(true, false))
                .build(),
            new Definition(context, Unit.primitives.get("string")),
            new Categorization(enumeration, unit),
            new Composition.Builder(enumeration, value)
                .cardinality(ONE_TO_MANY)
                .build(),
            new Definition(value, Unit.primitives.get("string")),
            new Categorization(descriptor, term),
            new Composition.Builder(descriptor, negation)
                .composer(new Composer(true, false))
                .build(),
            new Definition(negation, Unit.primitives.get("string")),
            new Composition(relation, mutant),
            new Definition(mutant, unit),
            new Composition.Builder(relation, mutation)
                .categorical(true)
                .build(),
            new Definition(mutation, term),
            new Composition.Builder(relation, operator)
                .categorical(true)
                .build()
        );

        units.forEach(molly::addUnit);
        descriptors.forEach(molly::addDescriptor);
        relations.forEach(molly::addRelation);

        return molly;
    }

    public static Language get(String languageName) {
        var key = languageName.toLowerCase();
        if (key.equals("kitchen")) return kitchen();
        if (key.equals("pbj")) return pbj();
        if (key.equals("blackjack")) return blackjack();
        if (key.equals("molly")) return molly();
        return null;
    }
}
