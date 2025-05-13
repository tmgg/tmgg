package io.tmgg.mgmt;

import io.tmgg.persistence.BaseEntity;
import io.tmgg.persistence.id.CustomId;
import io.tmgg.persistence.id.IdStyle;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@CustomId(prefix = Author.PREFIX ,style = IdStyle.DAILY_SEQ)
public class Author extends BaseEntity {

    public static final String PREFIX = "STU";


    String account;
    String name;
    Integer age;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    List<Book> books;

    public Author(String account, String name, Integer age) {
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
