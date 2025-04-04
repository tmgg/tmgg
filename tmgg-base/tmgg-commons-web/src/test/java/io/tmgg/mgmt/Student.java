package io.tmgg.mgmt;

import io.tmgg.lang.dao.DBConstants;
import io.tmgg.lang.dao.PersistEntity;
import io.tmgg.lang.dao.id.CustomId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Student implements PersistEntity {

    public static final String PREFIX = "STU_";


    String account;
    String name;
    Integer age;

    @Id
    @CustomId(prefix = "STU_")
    private String id;

    public Student(String account, String name, Integer age) {
        this.account = account;
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
                .append("id", getId())
                .append("account", account)
                .append("name", name)
                .append("age", age)
                .toString();
    }
}
