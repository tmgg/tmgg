package io.tmgg.web.persistence.converter;


import io.tmgg.data.converter.BaseCodeEnumConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class XConverter extends BaseCodeEnumConverter<UserType> {

}
