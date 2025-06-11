package io.tmgg.flowable.assignment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 主体， 如用户，角色，部门等
 */
@Getter
@Setter
public class Identity {
    String id;

    // 如果包含这个属性则表示树状
    @JsonProperty("pId")
    String pId;

    String label;
    String value;

    // 是否可选
    boolean isSelectable;
    boolean isUser;

    public Identity(String id, String pId, String label, boolean isUser, boolean isSelectable) {
        this.id = id;
        this.pId = pId;
        this.label = label;
        this.value = id;
        this.isSelectable = isSelectable;
        this.isUser = isUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Identity that = (Identity) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Identity{" +
               "id='" + id + '\'' +
               ", label='" + label + '\'' +
               '}';
    }
}
