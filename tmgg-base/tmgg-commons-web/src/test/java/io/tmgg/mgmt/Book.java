package io.tmgg.mgmt;


import io.tmgg.persistence.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

// 宠物

@NoArgsConstructor
@Getter
@Setter
@Entity
@FieldNameConstants
public class Book extends BaseEntity {
    String name;

    @ManyToOne
    Author author;

    @Lob
    String content;

    String publishDate;
    Integer saleCount;
    Integer favCount;


    public Book(String name, Author author) {
        this.name = name;
        this.author = author;
    }

    public Book(String name, String publishDate, Integer saleCount, Integer favCount) {
        this.name = name;
        this.publishDate = publishDate;
        this.saleCount = saleCount;
        this.favCount = favCount;
    }
}
