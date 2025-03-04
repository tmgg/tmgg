package io.tmgg.modules.sys.entity;

import io.tmgg.framework.dict.Dict;
import io.tmgg.framework.dict.DictItem;


@Dict(code = "orgType",label = "机构类型")
public interface OrgType {

  @DictItem(label = "单位")
  int UNIT = 10;

  @DictItem(label = "部门")
  int DEPT = 20;


  // 可在数据字典中添加更多



}


