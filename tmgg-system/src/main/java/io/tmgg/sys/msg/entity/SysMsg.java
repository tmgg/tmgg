package io.tmgg.sys.msg.entity;

import io.tmgg.lang.dao.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author 姜涛
 */
@Getter
@Setter
@Entity
public class SysMsg extends BaseEntity {


    @Column(length = 50)
    private String title;

    @Column(length = 500)
    private String content;


    @Column
    private Integer type;

    @Column
    private String link;





}
