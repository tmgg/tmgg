package io.tmgg.web.pojo.param;

import lombok.Data;

import java.util.List;

/**
 * 下拉选择后台请求参数
 */
@Data
public class OptionsParam {

    String searchText;

    /**
     * 默认选择的，
     * 方便返回到前端显示
     * 通常用于返回部分的情况
     */
    List<String> selected;



}
