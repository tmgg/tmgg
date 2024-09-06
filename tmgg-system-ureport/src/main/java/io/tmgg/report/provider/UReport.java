package io.tmgg.report.provider;

import io.tmgg.lang.dao.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.Date;


@Getter
@Setter
@Entity
@Table(name = "sys_ureport")
@FieldNameConstants
public class UReport extends BaseEntity {

    String id;

    String file;


    String content;

    Date updateTime;
}
