package io.tmgg.lang.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Transient;

public interface PersistEntity {

    void setId(String id);
    String getId();

    @JsonIgnore
    @Transient
    boolean isNew();
}
