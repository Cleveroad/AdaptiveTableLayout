package com.cleveroad.tablelayout.utils;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StringUtils {
    public static String toString(Iterable iterable, String separator) {
        StringBuilder stringBuilder = new StringBuilder();

        Collection<CharSequence> collection = filterNulls(iterable);
        for (CharSequence item : collection) {
            stringBuilder.append(String.valueOf(item));
            stringBuilder.append(separator);
        }

        if (stringBuilder.length() > 2) {
            return stringBuilder.substring(0, stringBuilder.length() - separator.length());
        } else return stringBuilder.toString();
    }

    @NonNull
    public static List<CharSequence> filterNulls(Iterable iterable) {
        List<CharSequence> result = new ArrayList<>();
        for (Object o : iterable) {
            if (o != null) {
                String string = String.valueOf(o);
                if (!TextUtils.isEmpty(string)) {
                    result.add(string);
                }
            }
        }
        return result;
    }
}
