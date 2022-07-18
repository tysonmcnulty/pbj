package com.vmware.pbj.molly;

import com.hypertino.inflector.English;

import java.util.Arrays;
import java.util.List;

public class EnglishUtils {

    public static String[] inflectionsOf(String word) {
        if (isPlural(word)) {
            return new String[] { English.singular(word), word };
        } else {
            return new String[] { word, English.plural(word) };
        }
    }

    private static boolean isPlural(String word) {
        return English.plural(English.singular(word)).equals(word);
    }

    public static boolean isNegatedPair(List<String> descriptions) {
        List<String> negators = Arrays.asList("not ", "non", "un");
        return descriptions.size() == 2 && negators.stream().anyMatch((n) -> descriptions.get(1).equals(n + descriptions.get(0)));
    }
}
