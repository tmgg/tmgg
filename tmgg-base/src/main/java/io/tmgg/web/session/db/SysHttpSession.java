package io.tmgg.web.session.db;

import cn.hutool.core.util.IdUtil;
import io.tmgg.lang.ann.Remark;
import io.tmgg.lang.dao.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.session.Session;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

// 参考 MapSession
@Remark("http会话")
@Getter
@Setter
@Entity
@FieldNameConstants
public class SysHttpSession extends BaseEntity implements Session, Serializable {


    private String originalId;

    @Column(columnDefinition = "blob")
    @Convert(converter = SessionAttrConverter.class)
    private HashMap<String, Object> sessionAttrs = new HashMap<>();


    Instant lastAccessedTime;

    boolean invalidated;



    Duration maxInactiveInterval;
    Instant creationTime;

    public SysHttpSession(){}
    public SysHttpSession(String id){
        this.id = id;
    }


    @Override
    public String changeSessionId() {
        String changedId = IdUtil.simpleUUID();
        setId(changedId);
        return changedId;
    }

    @Override
    public boolean isExpired() {
        return isExpired(Instant.now());
    }

    public boolean isExpired(Instant now) {
        if (this.maxInactiveInterval.isNegative()) {
            return false;
        }
        return now.minus(this.maxInactiveInterval).compareTo(this.lastAccessedTime) >= 0;
    }


    @Transient
    @Override
    public <T> T getAttribute(String attributeName) {
        return (T) this.sessionAttrs.get(attributeName);
    }
    @Transient
    @Override
    public Set<String> getAttributeNames() {
        return new HashSet<>(this.sessionAttrs.keySet());
    }

    @Override
    public void setAttribute(String attributeName, Object attributeValue) {
        if (attributeValue == null) {
            removeAttribute(attributeName);
        }
        else {
            this.sessionAttrs.put(attributeName, attributeValue);
        }
    }

    @Override
    public void removeAttribute(String attributeName) {
        this.sessionAttrs.remove(attributeName);
    }

    @Override
    public void prePersist() {
        super.prePersist();
        if (originalId == null) {
            originalId = id;
        }
        creationTime = Instant.now();
    }





    public static class SessionAttrConverter implements AttributeConverter<HashMap<String, Object>, byte[]> {

        @Override
        public byte[] convertToDatabaseColumn(HashMap<String, Object> attribute) {
            return SerializationUtils.serialize(attribute);
        }

        @Override
        public HashMap<String, Object> convertToEntityAttribute(byte[] dbData) {
            return SerializationUtils.deserialize(dbData);
        }
    }

}
