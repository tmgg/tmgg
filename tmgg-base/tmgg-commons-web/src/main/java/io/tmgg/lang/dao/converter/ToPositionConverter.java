package io.tmgg.lang.dao.converter;

import io.tmgg.jackson.JsonTool;
import io.tmgg.lang.obj.Position;

import java.io.IOException;

public class ToPositionConverter extends BaseConverter<Position> {

    @Override
    public Position convertToEntityAttribute(String dbData) {
        if(dbData != null){
            try {
                return JsonTool.jsonToBean(dbData, Position.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return  null;
    }
}
