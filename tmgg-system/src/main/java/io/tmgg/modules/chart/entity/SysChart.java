package io.tmgg.modules.chart.entity;

import io.tmgg.lang.ann.Msg;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.DBConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Entity
@Getter
@Setter
@FieldNameConstants
@Msg("系统图表")
public class SysChart extends BaseEntity {

    @Msg("名称")
    @NotNull
    @Column(length = 30)
    String name;

    @Msg("sql文本")
    @Lob
    @Column(columnDefinition = DBConstants.TYPE_LONGTEXT)
    String sqlContent;

    @Msg("分类")
    @Column(length = 30)
    String category;
}
