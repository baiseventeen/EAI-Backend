package com.njuzr.eaibackend.utils;

import org.apache.poi.ss.formula.functions.T;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/7 - 08:35
 * @Package: EAI-Backend
 */

public class ConvertUtil {

    public static <T> String listToString(List<T> list) {
        return list.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    public static <T> List<T> stringToList(String str, Function<String, T> converter) {
        return Arrays.stream(str.split(","))
                .map(String::trim)
                .map(converter)
                .collect(Collectors.toList());
    }
}
