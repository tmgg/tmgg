package io.tmgg.framework.dict;

import cn.hutool.core.util.ClassUtil;
import io.tmgg.BasePackage;
import io.tmgg.lang.SpringTool;
import io.tmgg.modules.sys.dao.SysDictDao;
import io.tmgg.modules.sys.dao.SysDictItemDao;
import io.tmgg.modules.sys.entity.SysDict;
import io.tmgg.modules.sys.entity.SysDictItem;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class DictAnnHandler {

    @Resource
    SysDictDao sysDictDao;

    @Resource
    SysDictItemDao sysDictItemDao;

    public void run() throws IllegalAccessException {
        log.info("开始解析字典注解");



        Set<Class<?>> classes = new HashSet<>();
        for (Class<?> cls : SpringTool.getBasePackageClasses()) {
            classes.addAll(ClassUtil.scanPackageByAnnotation(cls.getPackageName(), Dict.class));
        }

        for (Class cls : classes) {
            Dict dictAnn = (Dict) cls.getAnnotation(Dict.class);
            String code = dictAnn.code();
            String label = dictAnn.label();


            SysDict sysDict = new SysDict();
            sysDict.setId(code);
            sysDict.setCode(code);
            sysDict.setText(label);
            sysDict.setIsNumber(dictAnn.isNumber());
            sysDict = sysDictDao.save(sysDict);

            Field[] fields = cls.getFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                String key = String.valueOf(field.getInt(null));
                DictItem fieldAnnotation = field.getAnnotation(DictItem.class);
                Assert.notNull(fieldAnnotation, "需要有" + DictItem.class.getName() + "注解");

                String text = fieldAnnotation.label();


                SysDictItem data = new SysDictItem();
                data.setCode(key);
                data.setText(text);
                data.setSeq(i);
                data.setSysDict(sysDict);
                data.setId(code + "-" + key);
                data.setBuiltin(true);

                sysDictItemDao.save(data);
            }
        }
    }


}
