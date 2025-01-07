package io.tmgg.code.service;

import cn.hutool.core.util.StrUtil;
import io.tmgg.code.bean.BeanInfo;
import io.tmgg.code.bean.FieldInfo;
import io.tmgg.lang.AnnTool;
import io.tmgg.lang.ann.Remark;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class CodeGenService {


    public static final CharSequence[] IGNORE_FIELDS = {"createTime", "updateTime", "updateUser", "createUser",
            "updateUserLabel", "createTimeLabel", "extFields"};
    public static final CharSequence[] HIDE_IN_FORM = {"id","createUser", "createTime", "updateUser", "updateTime"};
    public static final CharSequence[] HIDE_IN_QUERY = {"id","createUser", "createTime", "updateUser", "updateTime"};

    public static final CharSequence[] HIDE_IN_LIST = {"id","createUser", "createTime", "updateUser"};
    public BeanInfo getBeanInfo(Class<?> cls) {

        BeanInfo bean = new BeanInfo();
        bean.setName(cls.getSimpleName());
        bean.setLabel(bean.getName());
        bean.setPackageName(cls.getPackage().getName());
        bean.setCls(cls);

        File root = getProjectRoot();
        bean.setProjectRoot(root);

        String modulePackage = bean.getPackageName().substring(0, bean.getPackageName().lastIndexOf("."));
        bean.setModulePackageName(modulePackage);
        bean.setModule(modulePackage.substring(modulePackage.lastIndexOf(".") + 1));

        CodeSource codeSource = cls.getProtectionDomain().getCodeSource();
        URL location = codeSource.getLocation();
        Assert.state(!location.toExternalForm().endsWith(".jar"), "不能是Jar包中的实体");
        String file = cls.getResource("./").getPath();

        String module = StringUtils.substringBeforeLast(file, "/target/");

        // target 目录的上一级
        bean.setMavenModule(new File(module).getName());
        if (bean.getProjectRoot().getName().equals(bean.getMavenModule())) {
            bean.setMavenModule(null);
        }

        File sourceFile = bean.getProjectRoot();
        if (bean.getMavenModule() != null) {
            sourceFile = new File(bean.getProjectRoot(), bean.getMavenModule());
        }
        sourceFile = new File(sourceFile, "src/main/java/" + ClassUtils.convertClassNameToResourcePath(cls.getName()) + ".java");
        bean.setSourceFile(sourceFile);
        bean.setModuleFile(sourceFile.getParentFile().getParentFile());
        {
            File mavenDir = bean.getProjectRoot();
            if (bean.getMavenModule() != null) {
                mavenDir = new File(bean.getProjectRoot(), bean.getMavenModule());
            }

            bean.setResourcesDir(new File(mavenDir,"src/main/resources"));
        }



        {
            // 获取code, 以表名为准
            String code = bean.getFirstLowerName();
            Table annotation = cls.getAnnotation(Table.class);
            if (annotation != null) {
                String tableName = annotation.name();
                if (tableName != null) {
                    code = StringUtils.capitalize(code);
                }
            }
            bean.setCode(code);
        }

        {
            if (cls.isAnnotationPresent(Remark.class)) {
                Remark remark = cls.getAnnotation(Remark.class);
                bean.setLabel(remark.value());
            }
        }

        List<Field> allFields = new ArrayList<>();
        Collections.addAll(allFields, cls.getDeclaredFields());

        Class<?> superclass = cls.getSuperclass();
        if (superclass != null) {
            Collections.addAll(allFields, superclass.getDeclaredFields());
        }

        for (Field field : allFields) {
            boolean isStatic = Modifier.isStatic(field.getModifiers());
            if (isStatic) {
                continue;
            }

            String fieldName = field.getName();


            if (StringUtils.equalsAny(fieldName, IGNORE_FIELDS)) {
                continue;
            }


            FieldInfo f = new FieldInfo();
            boolean isTransient = AnnTool.hasAnn(field, "Transient");

            if(!StrUtil.equalsAny(fieldName, HIDE_IN_QUERY)){
                if(!isTransient){
                    bean.getQueryFields().add(f);
                }
            }
            if(!StrUtil.equalsAny(fieldName, HIDE_IN_FORM)){
                if(!isTransient){
                    bean.getFormFields().add(f);
                }
            }


            boolean hideInForm = StringUtils.startsWithAny(fieldName, HIDE_IN_FORM);
            if (hideInForm) {
                f.setHideInForm(true);
                f.setHideInSearch(true);
            }


            f.setName(fieldName);
            f.setType(field.getType().getName());


            // 空检查
            if (field.isAnnotationPresent(NotNull.class)) {
                f.setRequired(true);
            }

            // 数据字典(enum)
            if (field.isAnnotationPresent(Enumerated.class)) {
                f.setDict(true);
                String presentableText = field.getType().getSimpleName();
                String dictTypeCode = StrUtil.toUnderlineCase(presentableText);
                f.setDictTypeCode(dictTypeCode);
            }

            // title 默认为name
            f.setTitle(f.getName());
            if (field.isAnnotationPresent(Remark.class)) {
                Remark remark = field.getAnnotation(Remark.class);
                f.setAnnRemark(remark.value());
                f.setTitle(f.getAnnRemark());
            }

            if(!StrUtil.equalsAny(fieldName, HIDE_IN_LIST)){
                bean.getListFields().add(f);
            }
            bean.getFieldInfoList().add(f);



        }

        String name = bean.getName();
        bean.setFirstLowerName(name.substring(0, 1).toLowerCase() + name.substring(1));
        bean.setUnderlineName(StrUtil.toUnderlineCase(name));

        return bean;
    }

    public  File getProjectRoot() {
        File root = new File("./").getAbsoluteFile();
        root = root.getParentFile();
        return root;
    }



}
