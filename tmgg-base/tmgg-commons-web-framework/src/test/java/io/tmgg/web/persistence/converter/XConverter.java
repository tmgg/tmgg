package io.tmgg.web.persistence.converter;


import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class XConverter extends BaseCodeEnumConverter<UserType> {

}
