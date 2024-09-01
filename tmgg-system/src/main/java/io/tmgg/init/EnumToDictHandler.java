package io.tmgg.init;

import io.tmgg.BasePackage;
import io.tmgg.lang.ann.Remark;
import io.tmgg.sys.entity.SysDictItem;
import io.tmgg.web.base.MessageEnum;
import io.tmgg.sys.dao.SysDictItemDao;
import io.tmgg.sys.dao.SysDictDao;
import io.tmgg.sys.entity.SysDict;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.hash.Hashing;
import io.tmgg.web.enums.CommonStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 根据枚举，自动生成数据字典
 */

@Component
@Slf4j
public class EnumToDictHandler implements Runnable {

    @Resource
    SysDictDao sysDictDao;

    @Resource
    SysDictItemDao sysDictItemDao;

    @Override
    public void run() {
        log.info("开始解析枚举，将枚举状态写入数据字典表中");

        Class<MessageEnum> superClass = MessageEnum.class;
        Set<Class<?>> classes = ClassUtil.scanPackageBySuper(BasePackage.BASE_PACKAGE, superClass);

        List<SysDictItem> dataList = new ArrayList<>();
        for (Class cls : classes) {
            Object[] enumConstants = cls.getEnumConstants();

            String simpleTypeName = cls.getSimpleName();
            Assert.state(cls.isAnnotationPresent(Remark.class), "枚举" + simpleTypeName + "保存为字典时失败，必须使用Comment注解描述");

            Remark annotation = (Remark) cls.getAnnotation(Remark.class);
            String label = annotation.value();


            log.info("发现枚举 {} {} ", simpleTypeName, annotation.value());

            SysDict sysDict = new SysDict();
            sysDict.setId(md5(simpleTypeName));
            sysDict.setCode(StrUtil.lowerFirst(simpleTypeName));
            sysDict.setName(label);
            sysDict.setBuiltin(true);
            sysDict = sysDictDao.save(sysDict);

            sysDictItemDao.deleteByTypeId(sysDict.getId());


            for (int i = 0; i < enumConstants.length; i++) {
                Object obj = enumConstants[i];
                MessageEnum me = (MessageEnum) obj;

                Enum e = (Enum) obj;
                String msg = me.getMessage();
                String name = e.name();

                String color = me.getColor().name().toLowerCase();

                SysDictItem data = new SysDictItem();
                data.setKey(name);
                data.setText(msg);
                data.setSeq(i);
                data.setSysDict(sysDict);
                data.setId(md5(simpleTypeName + name));
                data.setColor(color);
                data.setBuiltin(true);
                data.setStatus(CommonStatus.ENABLE);

                log.trace("{} {}={}", data.getId(), name, msg);
                dataList.add(data);
            }
        }
        sysDictItemDao.saveAll(dataList);
    }

    private String md5(String str) {
        long result = Hashing.md5().hashString(str, StandardCharsets.US_ASCII).asLong();
        return String.valueOf(result);
    }


}
