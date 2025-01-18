package io.tmgg.modules.report.entity;

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
public class SysReportChart extends BaseEntity {

    @Msg("名称")
    @NotNull
    @Column(length = 30)
    String title;

    @NotNull
    @Column(length = 30, unique = true)
    String code;

    @Msg("sql文本")
    @Lob
    @Column(name = "sql_", columnDefinition = DBConstants.TYPE_LONGTEXT)
    String sql;

    @Column(length = 10)
    String type;

    // 菜单PID
    String sysMenuPid;

    @Msg("查看次数")
    @NotNull
    Integer viewCount;

    @Override
    public void prePersist() {
        super.prePersist();
        if (viewCount == null) {
            viewCount = 0;
        }
    }
}
