
package io.tmgg.sys.entity;

import io.tmgg.lang.ann.Remark;
import io.tmgg.lang.dao.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.extern.slf4j.Slf4j;


/**
 * 参数配置
 */
@Slf4j
@Remark("系统配置")
@Getter
@Setter
@Entity
@FieldNameConstants
@ToString
public class SysConfig extends BaseEntity {

    @Remark("名称")
    @Column(length = 30, nullable = false)
    private String label;


    /**
     * 属性值
     */
    @Remark("值")
    private String value;

    @Remark("默认值")
    private String defaultValue;

    @Column(length = 20)
    private String valueType;


    @Remark("备注")
    private String remark;


    @Override
    public void prePersist() {
        super.prePersist();
        validateId();
    }

    private void validateId() {
        boolean ok = id.startsWith("sys.");
        if (ok) {
            log.info("主键:{}", id);
            // 为了和配置文件保持一致
            throw new IllegalArgumentException("主键必须以sys.开头。" + id);
        }
    }

    @Override
    public void preUpdate() {
        super.preUpdate();
        validateId();
    }
}
