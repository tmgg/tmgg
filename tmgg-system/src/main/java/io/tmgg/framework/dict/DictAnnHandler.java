package io.tmgg.framework.dict;

import cn.hutool.core.util.ClassUtil;
import io.tmgg.BasePackage;
import io.tmgg.lang.ann.Remark;
import io.tmgg.lang.ann.RemarkTool;
import io.tmgg.sys.dao.SysDictDao;
import io.tmgg.sys.dao.SysDictItemDao;
import io.tmgg.sys.entity.SysDict;
import io.tmgg.sys.entity.SysDictItem;
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
        for (String basePackage : BasePackage.getBasePackages()) {
            classes.addAll( ClassUtil.scanPackageByAnnotation(basePackage, Dict.class));
        }

        List<SysDictItem> dataList = new ArrayList<>();
        for (Class cls : classes) {
            Dict dictAnn = (Dict) cls.getAnnotation(Dict.class);
            String code = dictAnn.code();

            String simpleTypeName = cls.getSimpleName();
            Assert.state(cls.isAnnotationPresent(Remark.class), "枚举" + simpleTypeName + "保存为字典时失败，必须使用Remark注解描述");

            Remark annotation = (Remark) cls.getAnnotation(Remark.class);
            String label = annotation.value();

            log.info("发现枚举 {} {} ", simpleTypeName, annotation.value());

            SysDict sysDict = new SysDict();
            sysDict.setId(code);
            sysDict.setCode(code);
            sysDict.setName(label);
            sysDict.setBuiltin(true);
            sysDict.setIsNumber(dictAnn.isNumber());
            sysDict = sysDictDao.save(sysDict);

            Field[] fields = cls.getFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                String key = String.valueOf(field.getInt(null));
                String text = RemarkTool.getRemark(field);


                SysDictItem data = new SysDictItem();
                data.setKey(key);
                data.setText(text);
                data.setSeq(i);
                data.setSysDict(sysDict);
                data.setId(code + "-" + key);
                data.setBuiltin(true);

                log.trace("增加数据库字典 {} {}={}", data.getId(), key, text);
                dataList.add(data);
            }
        }
        sysDictItemDao.saveAll(dataList);
    }



}
