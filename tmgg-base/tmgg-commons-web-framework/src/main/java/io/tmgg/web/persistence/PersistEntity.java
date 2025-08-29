package io.tmgg.web.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface PersistEntity {

    void setId(String id);

    String getId();


    /**
     * 自定义id的值
     *
     * @return
     */
    @JsonIgnore
    default String getTempId() {
        return null;
    }

    default void setTempId(String id) {

    }


}
