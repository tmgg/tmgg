package io.tmgg.framework.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EntityExtraFieldConfigurer {
    private Class<?> entity;
    private String field;
    private String label;

}
