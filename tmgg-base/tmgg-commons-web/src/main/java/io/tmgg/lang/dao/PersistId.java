package io.tmgg.lang.dao;

import org.springframework.data.domain.Persistable;

public interface PersistId extends Persistable<String> {

    void setId(String id);

}
