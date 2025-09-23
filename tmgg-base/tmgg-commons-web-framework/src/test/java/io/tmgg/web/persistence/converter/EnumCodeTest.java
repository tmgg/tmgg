package io.tmgg.web.persistence.converter;

import io.tmgg.dbtool.DbTool;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@Slf4j
@SpringBootTest
public class EnumCodeTest {

    @Resource
    DbTool db;

    @Resource
    XUserDao xUserDao;


    @Test
    public void test() {
        log.info("begin test BaseEnumCodeConverter");
        XUser xUser = new XUser();
        xUser.setUserType(UserType.admin);
        xUser.setUserName("管理员_" + xUser.getUserType().code);
        xUserDao.save(xUser);


        Map<String, Object> map = db.findOne("select * from xuser where user_name=?", xUser.getUserName());

        String name = (String) map.get("userName");
        Integer type = (Integer) map.get("userType");
        Object id = map.get("id");

        String[] arr = name.split("_");
        Assertions.assertEquals(Integer.parseInt(arr[1]), type);

        System.out.println("name type" + name);
    }


}


