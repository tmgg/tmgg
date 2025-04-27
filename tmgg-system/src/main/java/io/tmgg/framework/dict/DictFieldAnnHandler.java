package io.tmgg.framework.dict;

import io.tmgg.modules.sys.dao.SysDictDao;
import io.tmgg.modules.sys.dao.SysDictItemDao;
import io.tmgg.modules.sys.entity.SysDict;
import io.tmgg.modules.sys.entity.SysDictItem;
import io.tmgg.modules.sys.service.JpaService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class DictFieldAnnHandler {

    @Resource
    SysDictDao sysDictDao;

    @Resource
    SysDictItemDao sysDictItemDao;

    @Resource
    JpaService jpaService;

    public void run() throws IllegalAccessException, IOException, ClassNotFoundException {
        List<Class<?>> classes = jpaService.findAllClass();

        for (Class cls : classes) {
            Field[] fields = cls.getDeclaredFields();
            for (Field field : fields) {
                handle(field);
            }
        }
    }

    private void handle(Field field) {
        DictField df = field.getAnnotation(DictField.class);
        if(df == null){
            return;
        }
        SysDict old = sysDictDao.findByCode(df.code());
        if(old != null){
           return;
        }

        SysDict sysDict = new SysDict();
        sysDict.setCode(df.code());
        sysDict.setText(df.label());
        sysDict.setIsNumber(true);
        sysDict = sysDictDao.save(sysDict);


        int[] value = df.value();
        String[] label = df.valueLabel();


        for (int i = 0; i < value.length; i++) {
            int v = value[i];
            String l = label[i];

            SysDictItem item = new SysDictItem();
            item.setCode(String.valueOf(v));
            item.setText(l);
            item.setSeq(i);
            item.setSysDict(sysDict);
            item.setBuiltin(true);

            sysDictItemDao.save(item);
        }
    }


}
