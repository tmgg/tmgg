package io.tmgg.modules.sys.controller;

import lombok.Data;

import java.util.List;

@Data
public class LoginUserVo {
    String name;
    String id;
    String avatar;
    Integer adminType;

    String orgName;
    String deptName;
    List<String> permissions;

    String roleNames;



    List<String> watermarkList;
}
