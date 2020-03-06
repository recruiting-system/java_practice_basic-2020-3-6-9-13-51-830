package com.thoughtworks.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class JsonUtil {
    private JsonUtil() {
    }

    public static final String STRING_WITH_QUOTES = "\"%s\"";

    /**
     * 把一个对象转换成json字符串
     * 只转换该对象的所有字段
     */
    public static String toJson(Object object) throws IllegalAccessException {
        final Field[] declaredFields = object.getClass().getDeclaredFields();

        Map<String, Object> jsonMap = new HashMap<>();
        for (Field declaredField : declaredFields) {
            final String key = String.format(STRING_WITH_QUOTES, declaredField.getName());

            if (!declaredField.isAccessible()) {
                declaredField.setAccessible(true);
            }

            Object value;
            // 判断是不是基本类型 基本数据类型的value不需要加引号
            if (declaredField.getType().isPrimitive()) {
                value = declaredField.get(object);
            } else {
                final Object fieldValue = declaredField.get(object);
                value = fieldValue == null ? null : String.format(STRING_WITH_QUOTES, fieldValue);
            }
            jsonMap.put(key, value);
        }

        final String jsonString = jsonMap.entrySet().stream()
                .filter(entry -> Objects.nonNull(entry.getValue()))
                .map(entry -> entry.getKey() + ":" + entry.getValue())
                .collect(Collectors.joining(","));
        return "{" + jsonString + "}";
    }
}
