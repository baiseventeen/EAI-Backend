package com.njuzr.eaibackend.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author: Leonezhurui
 * @Date: 2024/2/27 - 14:49
 * @Package: EAI-Backend
 */

public class PageMapperUtil {
    /**
     * 将分页结果中的每个对象从一种类型转换为另一种类型。
     * @param sourcePage 源分页结果
     * @param converter 转换函数
     * @param <T> 源对象类型
     * @param <V> 目标VO类型
     * @return 转换后的分页结果
     */
    public static <T, V> Page<V> convert(IPage<T> sourcePage, Function<T, V> converter) {
        Page<V> targetPage = new Page<>();
        targetPage.setRecords(sourcePage.getRecords().stream().map(converter).collect(Collectors.toList()));
        targetPage.setTotal(sourcePage.getTotal());
        targetPage.setCurrent(sourcePage.getCurrent());
        targetPage.setSize(sourcePage.getSize());
        return targetPage;
    }
}
