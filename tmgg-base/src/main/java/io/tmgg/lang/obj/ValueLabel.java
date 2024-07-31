package io.tmgg.lang.obj;

import lombok.Data;

import java.util.List;

@Data
public class ValueLabel {
    String label;
    Object value;

    List<ValueLabel> children;


    public static ValueLabel of(Object value, String label) {
        ValueLabel labelValue = new ValueLabel();

        labelValue.label = label;
        labelValue.value = value;

        return labelValue;
    }






}
