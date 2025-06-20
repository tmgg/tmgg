package io.tmgg.framework.dict;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import io.tmgg.lang.SpringTool;
import io.tmgg.lang.ann.Remark;
import io.tmgg.modules.system.dao.SysDictDao;
import io.tmgg.modules.system.dao.SysDictItemDao;
import io.tmgg.modules.system.entity.SysDict;
import io.tmgg.modules.system.entity.SysDictItem;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.HashSet;
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
        Set<Class<?>> classes = scanEnum();

        for (Class cls : classes) {
            Remark dictAnn = (Remark) cls.getAnnotation(Remark.class);

            String code = StrUtil.lowerFirst( cls.getSimpleName());
            String label = dictAnn.value();

            SysDict old = sysDictDao.findByIdOrCode(code);
            if(old != null){
                sysDictItemDao.deleteByPid(old.getId());
                sysDictDao.deleteById(old.getId());
            }

            SysDict sysDict = new SysDict();
            sysDict.setId(code);
            sysDict.setCode(code);
            sysDict.setText(label);
            sysDict.setIsNumber(false);
            sysDict = sysDictDao.save(sysDict);

            Field[] fields = cls.getFields();
            for (int i = 0; i < fields.length; i++) {

                Field field = fields[i];
                String key = field.getName();
                Remark fieldAnnotation = field.getAnnotation(Remark.class);
                Assert.notNull(fieldAnnotation, "需要有" + Remark.class.getName() + "注解");

                String text = fieldAnnotation.value();

                SysDictItem data = new SysDictItem();
                data.setCode(key);
                data.setText(text);
                data.setSeq(i);
                data.setSysDict(sysDict);
                data.setId(SecureUtil.md5(code + "-" + key));
                data.setBuiltin(true);

                sysDictItemDao.save(data);
            }
        }
    }


    @NotNull
    private static Set<Class<?>> scanEnum() {
        Set<Class<?>> result = new HashSet<>();
        Set<Class<?>> all = SpringTool.getBasePackageClasses();

        for (Class<?> superClass : all) {
            Set<Class<?>> set = ClassUtil.scanPackageByAnnotation(superClass.getPackageName(), Remark.class);
            for (Class<?> cls : set) {
                if(cls.isEnum()){
                    result.add(cls);
                }
            }
        }
        return result;
    }


}
