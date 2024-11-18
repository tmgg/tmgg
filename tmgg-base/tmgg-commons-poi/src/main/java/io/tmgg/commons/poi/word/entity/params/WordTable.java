package io.tmgg.commons.poi.word.entity.params;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * Word表格对象
 *
 * @author Pursuer
 * @version 1.0
 * @date 2023/4/20
 */
@Data
@Accessors(chain = true)
public class WordTable {
    /**
     * 表头（key：数据属性名，value：属性描述  例：{name:名称}）
     */
    private Map<String, String> headers;
    /**
     * 数据
     */
    private List<?> data;
}
