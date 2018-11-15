package com.cleveroad.sample.utils;

public class StringUtils {
    private StringUtils() {

    }

    public static String toString(Iterable iterable, String separator) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Object item : iterable) {
            stringBuilder.append(String.valueOf(item));
            stringBuilder.append(separator);
        }

        if (stringBuilder.length() > 2) {
            return stringBuilder.substring(0, stringBuilder.length() - separator.length());
        } else return stringBuilder.toString();
    }
}
