package com.vmware.pbj.molly;

import com.hypertino.inflector.English;

public class EnglishUtils {

    public static String[] singularAndPlural(String word) {
        if (isPlural(word)) {
            return new String[] { English.singular(word), word };
        } else {
            return new String[] { word, English.plural(word) };
        }
    }

    private static boolean isPlural(String word) {
        return English.plural(English.singular(word)).equals(word);
    }
}
