package com.tommy.catalog.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public class SlugUtil {
    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    public static String toSlug(String input){
        if(input==null||input.isEmpty()){
            return "";
        }

        // 1. Replace white space with -
        String noWhitespace = WHITESPACE.matcher(input).replaceAll("-");

        // 2. Remove Vietnamese punctuation
        String normalized = Normalizer.normalize(noWhitespace, Normalizer.Form.NFD);

        // 3. Remove special character
        String slug = NONLATIN.matcher(normalized).replaceAll("");

        // 4. Return to lower case and remove extra hyphens
        return slug.toLowerCase(Locale.ENGLISH)
                .replaceAll("-{2,}", "-")
                .replaceAll("^-|-$", "");
    }
}
