
package io.tmgg.core.enums;

import io.tmgg.lang.ann.Remark;
import io.tmgg.web.base.MessageEnum;
import io.tmgg.web.base.StatusColor;
import lombok.Getter;

/**
 * 审核状态
 */
@Remark("审核状态")
@Getter
public enum ApproveStatus implements MessageEnum {

    DRAFT("待提交"),
    PENDING("审核中"),
    APPROVED("审核通过"),
    REJECTED("审核未通过");

    private final String message;

    ApproveStatus(String message) {
        this.message = message;
    }

    @Override
    public StatusColor getColor() {
        switch (this) {
            case APPROVED:
                return StatusColor.SUCCESS;
            case REJECTED:
                return StatusColor.ERROR;
            case PENDING:
                return StatusColor.WARNING;
            case DRAFT:
                return StatusColor.DEFAULT;
            default:
                throw new IllegalArgumentException();
        }
    }
}
