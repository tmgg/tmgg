
package io.tmgg.modules.sys.entity;

import io.tmgg.lang.ann.Remark;
import io.tmgg.web.persistence.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;


@Remark("数据字典")
@Getter
@Setter
@Entity
@FieldNameConstants
public class SysDict extends BaseEntity {

    public SysDict() {
        super();
    }
    public SysDict(String id) {
        super(id);
    }

    @Column(length = 80)
    private String text;

    @Remark("编码")
    @Column(nullable = false, unique = true)
    private String code;




    // 是否数字类型，如1，2，3
    @Remark("是否数字类型")
    private Boolean isNumber;

}
