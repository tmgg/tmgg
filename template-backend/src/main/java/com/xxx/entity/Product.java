package com.xxx.entity;

import io.tmgg.web.persistence.BaseEntity;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Product extends BaseEntity {

    String name;
}
