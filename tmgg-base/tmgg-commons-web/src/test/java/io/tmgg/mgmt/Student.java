package io.tmgg.mgmt;

import io.tmgg.lang.dao.BaseEntity;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Student extends BaseEntity {
    String account;
    String name;

    Integer age;
}
