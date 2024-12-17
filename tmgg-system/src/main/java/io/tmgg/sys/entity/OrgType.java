package io.tmgg.sys.entity;

import io.tmgg.framework.dict.Dict;
import io.tmgg.lang.ann.Remark;



@Dict(code = "ORG_TYPE")
@Remark("机构类型")
public interface OrgType {

  @Remark("单位")
  int UNIT = 10;

  @Remark("部门")
  int DEPT = 20;


  // 可在数据字典中添加更多



}


