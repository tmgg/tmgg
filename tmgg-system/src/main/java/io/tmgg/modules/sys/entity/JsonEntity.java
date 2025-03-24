package io.tmgg.modules.sys.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.net.URI;
import java.util.Map;

/**
 * 从database目录存储的json数据实体
 */
@Data
@NoArgsConstructor
public class JsonEntity implements Serializable {
    private String entityName;
    private Map<String, Object> data;

    private Persistable<String> entity;

    private boolean update = true;
    private String findField = "id";
    private Object findValue = null;

    private URI uri;

    public JsonEntity(String entityName, Map<String, Object> data, URI uri) {
        this.entityName = entityName;
        this.data = data;
        this.uri = uri;
    }
}
