package io.tmgg.sys.msg.entity;

import io.tmgg.lang.dao.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;

/**
 * @author 姜涛
 */
@Getter
@Setter
@Entity
public class SysMsg extends BaseEntity {


    @Column(length = 50)
    private String title;


    @Lob
    private String content;


    @Column
    private Integer type;

    @Column
    private String link;


    private String topic;




}
