package com.tommy.common.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public class SlugUtil {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]+");
    private static final Pattern EDGES = Pattern.compile("(^-+)|(-+$)");

    public static String toSlug(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }

        // 1. Chuyển tiếng Việt có dấu sang không dấu
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String noDiacritics = pattern.matcher(normalized).replaceAll("");

        // Thay thế chữ Đ/đ đặc trưng của tiếng Việt
        noDiacritics = noDiacritics.replace("đ", "d").replace("Đ", "d");

        // 2. Chuyển khoảng trắng thành gạch ngang
        String slug = WHITESPACE.matcher(noDiacritics).replaceAll("-");

        // 3. Chuyển về chữ thường và lọc ký tự không hợp lệ
        slug = slug.toLowerCase(Locale.ENGLISH);
        slug = NONLATIN.matcher(slug).replaceAll("");

        // 4. Loại bỏ gạch ngang dư thừa ở đầu và cuối chuỗi
        slug = EDGES.matcher(slug).replaceAll("");

        return slug;
    }
}
