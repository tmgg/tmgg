
package io.tmgg.modules.sys.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.tmgg.lang.ann.Msg;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.DBConstants;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Msg("字典项")
@Getter
@Setter
@Entity
@FieldNameConstants
public class SysDictItem extends BaseEntity {


    @NotNull
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    SysDict sysDict;


    @NotNull
    @Msg("键")
    @Column(length = 50)
    String code;




    @Msg("文本")
    private String text;



    @Column(nullable = false)
    private Boolean enabled;

    @Msg("颜色")
    @Column(length = 10)
    private String color;

    @Msg("系统内置")
    @NotNull
    private Boolean builtin;


    @Msg("序号")
    private Integer seq;




    @Override
    public void prePersistOrUpdate() {
        if(seq == null){
            seq = 0;
        }
        if(enabled == null){
            enabled = true;
        }
    }


}
