package io.tmgg.kettle.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransXmlInfo {



    @JacksonXmlProperty(localName = "info")
    Info info;


    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @Setter
    public static class Info {
        String name;
        String description;

        String directory;
    }


}


