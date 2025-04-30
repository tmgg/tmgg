package io.tmgg.web;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * 基本的查询字段，如果字段不足，可继承
 */
@Getter
@Setter
public class CommonQueryParam {

    String keyword;


    /**
     *  其他字段
     */
    @JsonAnySetter
    private Map<String, Object> data = new HashMap<>();


    @JsonAnyGetter
    public Map<String,Object> getData(){
        return data;
    }



}

