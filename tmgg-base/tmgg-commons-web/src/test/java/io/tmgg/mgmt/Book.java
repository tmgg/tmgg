package io.tmgg.mgmt;


import io.tmgg.lang.dao.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 宠物

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Book extends BaseEntity {
    String name;

    @ManyToOne
    Author author;

    public Book(String name, Author author) {
        this.name = name;
        this.author = author;
    }


}
