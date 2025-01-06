package io.tmgg.code.bean;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Data
public class BeanInfo {

    // 为空则表示一个项目，没有子模块
    String mavenModule;
    File sourceFile;
    File moduleFile;
    File resourcesDir;

    String modulePackageName;
    String packageName;
    String name;
    String module;

    String code; // 权限， api的标识

    // 首先从注释中获得， 如果没有则设置为实体名称
    String label;


    String firstLowerName;

    String underlineName;



    Class<?> cls;

    File projectRoot;


    List<FieldInfo> fieldInfoList = new ArrayList<>();


    List<FieldInfo> queryFields = new ArrayList<>();

    List<FieldInfo> formFields = new ArrayList<>();

    List<FieldInfo> listFields = new ArrayList<>();
}
