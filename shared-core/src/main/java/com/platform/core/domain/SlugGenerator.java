package com.platform.core.domain;

import java.text.Normalizer;
import java.util.UUID;

public final class SlugGenerator {

    private SlugGenerator() {}

    public static String generate(String input) {
        if (input == null || input.isBlank()) {
            return UUID.randomUUID().toString().substring(0, 8);
        }

        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String slug = normalized
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("[\\s]+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");

        if (slug.length() > 200) {
            slug = slug.substring(0, 200);
        }

        return slug;
    }

    public static String generateUnique(String input) {
        String slug = generate(input);
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        return slug + "-" + suffix;
    }
}