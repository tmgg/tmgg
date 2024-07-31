
package io.tmgg.web.enums;


import io.tmgg.lang.ann.Remark;
import io.tmgg.web.base.MessageEnum;
import io.tmgg.web.base.StatusColor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Remark("审批状态")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum AuditStatus implements MessageEnum {

    REJECT("退回", StatusColor.RED),
    SUBMIT("提报",StatusColor.BLUE),
    APPROVE("通过",StatusColor.GREEN),
    DRAFT("草稿", StatusColor.GRAY);


    private  String message;
    private  StatusColor color;



    @Override
    public StatusColor getColor() {
       return color;
    }

}
