package io.tmgg.lang.dao.test;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import io.tmgg.TestBootApplication;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.mgmt.Student;
import io.tmgg.mgmt.StudentDao;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@SpringBootTest(classes = TestBootApplication.class)
public class JpaQueryTest {


    public static final String NAME_NOT_EXIST = "名字不存在";

    public static final List<String> ONE_NULL_LIST = ListUtil.of((String) null);

    @Resource
    StudentDao dao;

    @BeforeEach
    public void init() {
        dao.deleteAllInBatch();
        dao.save(new Student("zhangsan", "张三", 3));
        dao.save(new Student("lisi", "李四", 4));
        dao.save(new Student("wangwu", "王五", 5));
        dao.save(new Student("x", null, null));
    }

    @Test
    public void test() {
        JpaQuery<Student> q = new JpaQuery<>();
        q.eq("account", "lisi");
        Assertions.assertTrue(dao.exists(q));

        q.clear();
        q.isNull("name");
        Assertions.assertEquals(dao.findOne(q).getAccount(), "x");


        q.clear();
        q.isNotNull("name");
        Assertions.assertEquals(3L, dao.count(q));


        q.clear();
        q.ne("account", "zhangsan");
        Assertions.assertEquals(3L, dao.count(q));


        q.clear();
        q.between("age", 3, 5);
        Assertions.assertEquals(3L, dao.count(q));

        q.clear();
        q.between("age", 3, 4);
        Assertions.assertEquals(2L, dao.count(q));

        q.clear();
        q.notBetween("age", 3, 4);
        Assertions.assertEquals(1L, dao.count(q));
    }


    @Test
    public void testIn() {
        JpaQuery<Student> q = new JpaQuery<>();

        q.clear();
        q.in("name", "张三");
        Assertions.assertEquals(1L, dao.count(q));

        q.clear();
        q.in("name", NAME_NOT_EXIST);
        Assertions.assertEquals(0L, dao.count(q));


        q.clear();
        q.in("name", "张三", "李四", null);
        Assertions.assertEquals(3L, dao.count(q));



        q.clear();
        q.in("name", ONE_NULL_LIST);
        Assertions.assertEquals(1L, dao.count(q));


        q.clear();
        q.in("name", Collections.emptyList());
        Assertions.assertEquals(0L, dao.count(q));

        q.clear();
        q.in("account", "zhangsan", "lisi");
        Assertions.assertEquals(2L, dao.count(q));

        q.clear();
        q.in("account", "zhangsan", "lisi", StrUtil.uuid());
        Assertions.assertEquals(2L, dao.count(q));

    }

    @Test
    public void testNotIn() {
        JpaQuery<Student> q = new JpaQuery<>();

        q.notIn("name", "张三", "李四");
        Assertions.assertEquals(2L, dao.count(q));

        q.clear();
        q.notIn("name", "张三");
        Assertions.assertEquals(3L, dao.count(q));

        q.clear();
        q.notIn("name", "张三", "李四", null);
        Assertions.assertEquals(1L, dao.count(q));


        q.clear();
        q.notIn("name", "张三",  null);
        Assertions.assertEquals(2L, dao.count(q));


        q.clear();
        q.notIn("name",   ONE_NULL_LIST);
        print(q);
        Assertions.assertEquals(3L, dao.count(q));
    }


    private void print(JpaQuery<Student> q) {
        List<Student> list = dao.findAll(q);
        if(list.isEmpty()){
            System.out.println("查询结果为空");
        }
        list.forEach(System.out::println);
        System.out.println();
        System.out.println();
    }

}
