package io.tmgg.sys.org.enums;

import io.tmgg.lang.ann.Remark;
import io.tmgg.web.base.MessageEnum;
import io.tmgg.web.base.StatusColor;
import lombok.Getter;


// 组织类型
@Remark("组织类型")
@Getter
public enum OrgType implements MessageEnum {

  UNIT("单位", false), // 单位
  DEPT("部门", true); // 部门

   /* DANG_WEI( "党委"),
    DANG_GONG_WEI( "党工委"),
    DANG_ZONG_ZHI( "党总支"),
    DANG_ZHI_BU( "党支部");*/

  private final String message;

  /**
   * 标志是否一个单位
   */
  private final boolean dept;

  OrgType(String message, boolean isDept) {
    this.message = message;
    this.dept = isDept;
  }

  @Override
  public StatusColor getColor() {
    if (this == DEPT) {
      return StatusColor.WARNING;
    }
    return StatusColor.SUCCESS;
  }


  public boolean isDept() {
    return dept;
  }
}
