package com.njuzr.eaibackend.utils;

import com.njuzr.eaibackend.exception.MyException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

/**
 * @author: Leonezhurui
 * @Date: 2024/2/15 - 10:08
 * @Package: EAI-Backend
 */

@Slf4j
public class ModelMapperUtil {
    private static final ModelMapper modelMapper = new ModelMapper();

    static {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT); // 开启严格模式，只有字段完全匹配才会映射
    }

    private ModelMapperUtil() {
        // 私有构造函数，防止实例化
    }


    // 1 将entity转化成outClass类的实例对象
    public static <D, T> D map(final T entity, Class<D> outClass) {
        try {
            return modelMapper.map(entity, outClass);
        }catch (Exception e) {
            log.error("对象转换错误：从 {} 到 {}", entity.getClass().getName(), outClass.getName(), e);
            throw new MyException(502, "对象转换错误");
        }
    }

    // 2 将source转化成destination对象
    public static <D, T> void map(final T source, D destination) {
        try {
            modelMapper.map(source, destination);
        }catch (Exception e) {
            log.error("对象转换错误：从 {} 到 {}", destination.getClass().getName(), source.getClass().getName(), e);
            throw new MyException(502, "对象转换错误");
        }
    }
}
