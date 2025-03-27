package io.tmgg.init;

import io.tmgg.BasePackage;
import io.tmgg.lang.ann.Msg;
import io.tmgg.modules.sys.entity.SysDictItem;
import io.tmgg.web.base.DictEnum;
import io.tmgg.modules.sys.dao.SysDictItemDao;
import io.tmgg.modules.sys.dao.SysDictDao;
import io.tmgg.modules.sys.entity.SysDict;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.hash.Hashing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 根据枚举，自动生成数据字典
 */

@Component
@Slf4j
public class DictEnumHandler {

    @Resource
    SysDictDao sysDictDao;

    @Resource
    SysDictItemDao sysDictItemDao;

    public void run() {
        log.info("开始解析枚举，将枚举状态写入数据字典表中");

        Class<DictEnum> superClass = DictEnum.class;
        Set<Class<?>> classes = new HashSet<>();
        for (String basePackage : BasePackage.getBasePackages()) {
            classes.addAll( ClassUtil.scanPackageBySuper(basePackage, superClass));
        }

        for (Class cls : classes) {
            Object[] enumConstants = cls.getEnumConstants();

            String simpleTypeName = cls.getSimpleName();
            Assert.state(cls.isAnnotationPresent(Msg.class), "枚举" + simpleTypeName + "保存为字典时失败，必须使用" + Msg.class.getSimpleName() + "注解描述");

            Msg annotation = (Msg) cls.getAnnotation(Msg.class);
            String label = annotation.value();


            log.info("发现枚举 {} {} ", simpleTypeName, annotation.value());

            SysDict sysDict = new SysDict();
            sysDict.setId(md5(simpleTypeName));
            sysDict.setCode(StrUtil.lowerFirst(simpleTypeName));
            sysDict.setName(label);
            sysDict.setBuiltin(true);
            sysDict = sysDictDao.save(sysDict);

            sysDictItemDao.deleteByPid(sysDict.getId());


            for (int i = 0; i < enumConstants.length; i++) {
                Object obj = enumConstants[i];
                DictEnum me = (DictEnum) obj;

                Enum e = (Enum) obj;
                String msg = me.getMessage();
                String name = e.name();

                String color = me.getColor().name().toLowerCase();

                SysDictItem data = new SysDictItem();
                data.setCode(name);
                data.setText(msg);
                data.setSeq(i);
                data.setSysDict(sysDict);
           //     data.setId(md5(simpleTypeName + name));
                data.setColor(color);
                data.setBuiltin(true);

                log.trace("{} {}={}", data.getId(), name, msg);
                sysDictItemDao.save(data);
            }
        }
    }

    private String md5(String str) {
        long result = Hashing.md5().hashString(str, StandardCharsets.US_ASCII).asLong();
        return String.valueOf(result);
    }


}
