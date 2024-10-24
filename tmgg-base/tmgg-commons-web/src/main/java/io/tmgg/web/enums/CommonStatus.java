
package io.tmgg.web.enums;


import io.tmgg.lang.ann.Remark;
import io.tmgg.web.base.DictEnum;
import io.tmgg.web.base.StatusColor;
import lombok.Getter;


@Remark("数据状态")
@Getter
public enum CommonStatus implements DictEnum {

    DISABLE("停用"),
    ENABLE("正常");

    private final String message;

    CommonStatus(String message) {
        this.message = message;
    }

    @Override
    public StatusColor getColor() {
        if(this == ENABLE){
            return StatusColor.SUCCESS;
        }
        if(this == DISABLE){
            return StatusColor.ERROR;
        }

        return StatusColor.DEFAULT;
    }

}
