
package io.tmgg.sys.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.tmgg.lang.ann.Remark;
import io.tmgg.web.enums.CommonStatus;
import io.tmgg.lang.dao.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Remark("字典项")
@Getter
@Setter
@Entity
@FieldNameConstants
public class SysDictItem extends BaseEntity {


    @NotNull
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    SysDict sysDict;


    @Remark("键")
    @Column(name = "key_",length = 50)
    String key;

    @Remark("文本")
    private String text;



    @Remark("状态")
    @Enumerated(EnumType.STRING)
    private CommonStatus status;

    @Remark("颜色")
    @Column(length = 10)
    private String color;

    @Remark("系统内置")
    @NotNull
    private Boolean builtin;


    @Remark("序号")
    private Integer seq;




    @Override
    public void prePersistOrUpdate() {
        if (status == null) {
            status = CommonStatus.ENABLE;
        }
        if(seq == null){
            seq = 0;
        }
    }


}
