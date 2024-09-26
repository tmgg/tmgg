package io.tmgg.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Field {

    String label;
    String name;
    String value;

    boolean required;

    String valueType;
    String valueTypeParams;

}
