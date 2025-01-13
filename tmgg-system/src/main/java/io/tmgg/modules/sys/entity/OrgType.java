package io.tmgg.modules.sys.entity;

import io.tmgg.framework.dict.Dict;
import io.tmgg.framework.dict.DictItem;
import io.tmgg.lang.ann.Remark;



@Dict(code = "ORG_TYPE",label = "机构类型")
public interface OrgType {

  @DictItem(label = "单位")
  int UNIT = 10;

  @DictItem(label = "部门")
  int DEPT = 20;


  // 可在数据字典中添加更多



}


