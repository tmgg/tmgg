package io.github.tmgg.test;

import cn.hutool.setting.yaml.YamlUtil;
import io.tmgg.modules.system.entity.SysConfig;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.Writer;

public class YamlTest {

    @Test
    public void beanToYaml(){
        SysConfig sysConfig = new SysConfig();

        YamlUtil.dump(sysConfig, new PrintWriter(System.out));


    }

}
