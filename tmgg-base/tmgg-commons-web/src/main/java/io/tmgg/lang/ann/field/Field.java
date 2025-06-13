package io.tmgg.lang.ann.field;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Field {

    String label;
    String name;

    boolean required;

    String defaultValue;

    String componentType ;

    Map<String,Object> componentProps ;


}
