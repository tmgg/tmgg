package io.tmgg.framework.dict;

import io.tmgg.modules.sys.dao.SysDictDao;
import io.tmgg.modules.sys.dao.SysDictItemDao;
import io.tmgg.modules.sys.entity.SysDict;
import io.tmgg.modules.sys.entity.SysDictItem;
import io.tmgg.modules.sys.service.JpaService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;
import java.lang.reflect.Field;
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
        DictField dictField = field.getAnnotation(DictField.class);
        if(dictField == null){
            return;
        }
        SysDict old = sysDictDao.findByCode(dictField.code());
        if(old != null){
           return;
        }

        SysDict sysDict = new SysDict();
        sysDict.setCode(dictField.code());
        sysDict.setText(dictField.label());
        sysDict.setIsNumber(true);
        sysDict = sysDictDao.save(sysDict);


        String items = dictField.items();
        String[] arr = items.split(",");
        for (int i = 0; i < arr.length; i++) {
            String kv = arr[i];
            String[] kvArr = kv.split("-");
            Assert.state(kvArr.length ==2, "配置错误" + items);
            String k = kvArr[0].trim();
            String v = kvArr[1].trim();


            SysDictItem item = new SysDictItem();
            item.setCode(k);
            item.setText(v);
            item.setSeq(i);
            item.setSysDict(sysDict);
            item.setBuiltin(true);
            sysDictItemDao.save(item);
        }

    }


}
