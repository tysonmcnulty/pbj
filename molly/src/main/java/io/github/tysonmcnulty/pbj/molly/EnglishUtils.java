package io.github.tysonmcnulty.pbj.molly;

import com.hypertino.inflector.English;
import org.apache.commons.text.WordUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EnglishUtils {

    private static final Set<String> ABNORMAL_SINGULAR_WORDS = new HashSet<>(List.of(
        "slice"
    ));

    public static String[] inflectionsOf(String word) {
        if (isPlural(word)) {
            return new String[] { English.singular(word), word };
        } else {
            return new String[] { word, English.plural(word) };
        }
    }

    private static boolean isPlural(String word) {
        if (ABNORMAL_SINGULAR_WORDS.contains(word)) return false;
        return English.plural(English.singular(word)).equals(word);
    }

    public static boolean isNegatedPair(List<String> descriptions) {
        List<String> negators = Arrays.asList("not ", "non", "un", "in");
        return descriptions.size() == 2 && negators.stream().anyMatch((n) -> descriptions.get(1).equals(n + descriptions.get(0)));
    }

    public static String normalizeCase(String str) {
        if (str.equals(str.toUpperCase())) return str;

        return WordUtils.uncapitalize(str);
    }
}
