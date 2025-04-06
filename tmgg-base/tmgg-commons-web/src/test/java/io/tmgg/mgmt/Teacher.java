package io.tmgg.mgmt;

import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.id.CustomId;
import io.tmgg.lang.dao.id.IdStyle;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Getter
@Setter
@NoArgsConstructor
@CustomId(prefix = Teacher.PREFIX ,style = IdStyle.DAILY_SEQ,length = 16)
public class Teacher extends BaseEntity {

    public static final String PREFIX = "USR_";


    String name;

    public Teacher(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
                .append("id", getId())
                .append("name", name)
                .toString();
    }
}
