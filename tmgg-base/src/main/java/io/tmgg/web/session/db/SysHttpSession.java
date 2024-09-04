package io.tmgg.web.session.db;

import io.tmgg.lang.ann.Remark;
import io.tmgg.lang.dao.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.apache.commons.lang3.SerializationUtils;
import org.checkerframework.checker.units.qual.N;
import org.springframework.session.MapSession;

import java.time.Duration;
import java.util.Date;

@Remark("http会话")
@Getter
@Setter
@Entity
@FieldNameConstants
public class SysHttpSession extends BaseEntity {


    @Column(length = 60, nullable = false, unique = true)
    String sessionId;

    @Lob
    @NotNull
    @Column(columnDefinition = "blob")
    @Convert(converter = SessionConverter.class)
    MapSession session;


    Date lastAccessedTime;

    boolean invalidated;

    boolean expired;

    Duration maxInactiveInterval;


    public static class SessionConverter implements AttributeConverter<MapSession, byte[]> {

        @Override
        public byte[] convertToDatabaseColumn(MapSession attribute) {
            return SerializationUtils.serialize(attribute);
        }

        @Override
        public MapSession convertToEntityAttribute(byte[] dbData) {
            return SerializationUtils.deserialize(dbData);
        }
    }

}
