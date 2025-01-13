package io.tmgg.framework.session;

import cn.hutool.core.util.IdUtil;
import io.tmgg.lang.ann.Msg;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.session.Session;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

// 参考 MapSession
@Msg("http会话")
@Getter
@Setter
@FieldNameConstants
@Slf4j
@ToString
public class SysHttpSession  implements Session, Serializable {
    public static final String SUBJECT_KEY = "SUBJECT";


    private String id;


    private String originalId;

    private HashMap<String, Object> sessionAttrs = new HashMap<>();


    Instant lastAccessedTime;


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



}
