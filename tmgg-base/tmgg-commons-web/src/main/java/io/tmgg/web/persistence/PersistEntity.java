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
    default String getCustomId() {
        return null;
    }

    default void setCustomId(String id) {

    }


}
