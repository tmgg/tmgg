package io.tmgg.kettle.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobXmlInfo {

    String name;
    String description;

    @JacksonXmlProperty(localName = "modified_date")
    String modifiedDate;

    List<Parameter> parameters;


    @Getter
    @Setter
    public static class Parameter {
        String name;
        @JacksonXmlProperty(localName = "default_value")
        String defaultValue;
        String description;
    }


}


