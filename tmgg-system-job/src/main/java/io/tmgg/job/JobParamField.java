package io.tmgg.job;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobParamField {
    String label;
    String name;
    String value;

    boolean required;
}
