package io.tmgg.web.session.db;

import jakarta.persistence.AttributeConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;

import java.util.HashMap;

@Slf4j
public class SessionAttrConverter implements AttributeConverter<HashMap<String, Object>, byte[]> {

    @Override
    public byte[] convertToDatabaseColumn(HashMap<String, Object> attribute) {
        return SerializationUtils.serialize(attribute);
    }

    @Override
    public HashMap<String, Object> convertToEntityAttribute(byte[] dbData) {
        if (dbData == null) {
            return new HashMap<>();
        }
        try {
            HashMap<String, Object> deserialize = SerializationUtils.deserialize(dbData);
            return deserialize;
        } catch (Exception e) {
            log.info("反序列化失败", e);
            return null;
        }
    }
}
