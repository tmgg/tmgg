package io.tmgg.modules.system.dto.response;

import io.tmgg.commons.poi.excel.annotation.Excel;
import io.tmgg.lang.ann.Remark;
import lombok.Data;

import java.util.List;

@Data
public class UserResponse {

    private  String id;

    @Remark("所属机构")
    private String unitId;

    @Excel(name = "单位")
    private String unitLabel;

    @Remark("所属部门")
    private String deptId;

    @Excel(name = "部门")
    private String deptLabel;


    @Excel(name = "账号")
    private String account;



    @Excel(name = "姓名")
    private String name;


    @Excel(name = "电话")
    private String phone;

    @Excel(name = "邮箱")
    private String email;

    private Boolean enabled;

    private  List<String> roleNames;




    // 扩展字段1
    private String extra1;
    private String extra2;
    private String extra3;
}
