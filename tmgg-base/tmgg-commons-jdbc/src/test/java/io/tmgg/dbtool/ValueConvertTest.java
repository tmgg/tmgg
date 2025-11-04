package io.tmgg.dbtool;

import cn.hutool.core.bean.BeanUtil;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValueConvertTest {

    DbTool db;

    @BeforeEach
    public void before() {

        String sql = """
                CREATE TABLE product (
                       id int ,
                       name varchar(50)  ,
                       product_type int ,
                       product_type_str varchar(50)  ,
                       price decimal(10,2) ,
                       tags varchar(255) ,
                       create_time timestamp 
                     )
                """;

        db.execute(sql);

        Map<String, Object> data = new HashMap<>();
        data.put("id", "1");
        data.put("name", "草莓");
        data.put("product_type", 2);
        data.put("product_type_str", "FOOD");
        data.put("price", 19.9);
        data.put("tags", "0,1,2,3");
        data.put("create_time", new Date());
        db.insert("product", data);


    }


    // TODO 初始化测试db
  //  @Test
    public void test() {
        Product p = db.findOne(Product.class, "select * from product where id=1");
        System.out.println(p);

        checkAnyNull(p);


        Product2 p2 = db.findOne(Product2.class, "select * from product where id=1");


        checkAnyNull(p2);

        List<Integer> tags = p2.getTags();
        for (int i = 0; i < tags.size(); i++) {
            Integer tag = tags.get(i);
            Assertions.assertEquals(Integer.class, tag.getClass());
            Assertions.assertEquals(i, tag);
        }


    }


    private void checkAnyNull(Object bean) {
        System.out.println(bean);

        for (Field f : bean.getClass().getDeclaredFields()) {
            Object value = BeanUtil.getFieldValue(bean, f.getName());
            System.out.println(f.getName() + ":" + value);
            Assertions.assertNotNull(value);
        }
    }
}
