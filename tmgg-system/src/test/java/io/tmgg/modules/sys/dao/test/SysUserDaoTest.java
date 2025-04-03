package io.tmgg.modules.sys.dao.test;

import io.tmgg.SysBootApplication;
import io.tmgg.modules.sys.dao.SysUserDao;
import io.tmgg.modules.sys.entity.SysUser;
import jakarta.annotation.Resource;
import org.apache.derby.jdbc.EmbeddedDriver;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(classes = SysBootApplication.class)
public class SysUserDaoTest {


    @Resource
    SysUserDao sysUserDao;

    @Test
    public void test() {
        SysUser sysUser = new SysUser();
        sysUser.setName("张三");
        sysUser.setAccount("zhangsan");
        sysUser.setEnabled(true);

        sysUserDao.save(sysUser);
    }

}
