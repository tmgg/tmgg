
package io.tmgg.framework.enums;

import io.tmgg.lang.ann.Remark;
import io.tmgg.web.base.StatusColor;
import lombok.Getter;


@Remark("审核状态")
@Getter
public enum ApproveStatus  {

    @Remark("待提交")
    DRAFT,

    @Remark("审核中")
    PENDING,

    @Remark("审核通过")
    APPROVED,

    @Remark("审核未通过")
    REJECTED;


}
