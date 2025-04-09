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
@CustomId(prefix = Student.PREFIX ,style = IdStyle.DAILY_SEQ)
public class Student extends BaseEntity {

    public static final String PREFIX = "STU";


    String account;
    String name;
    Integer age;



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
