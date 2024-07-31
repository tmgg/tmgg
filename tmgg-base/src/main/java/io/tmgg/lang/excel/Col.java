package io.tmgg.lang.excel;

import lombok.Builder;
import lombok.Data;

import java.util.function.Function;

@Data
@Builder
public class Col<T> {
    String title;
    String dataIndex; // 支持小数点 如 "org.name"
    Function<T, Object> render;

    int chars; // 字符数，汉字算2个


}
