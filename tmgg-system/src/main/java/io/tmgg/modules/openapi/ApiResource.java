package io.tmgg.modules.openapi;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Method;

@Data
@AllArgsConstructor
public class ApiResource {

    Object bean;
    Method method;

    OpenApi openApi;
}
