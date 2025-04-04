package io.tmgg.lang.dao.test;

import io.tmgg.TestBootApplication;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.mgmt.Student;
import io.tmgg.mgmt.StudentDao;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest(classes = TestBootApplication.class)
public class JpaQueryTest {


    // 由于base到是个抽象对象，这里以DbCache为例
    @Resource
    StudentDao dao;

    @Test
    public void test() {
        dao.save(new Student("zhangsan","张三",3));
        dao.save(new Student("lisi","李四",4));
        dao.save(new Student("wangwu","王五",5));
        dao.save(new Student("x", null,null));


        JpaQuery<Student> q = new JpaQuery<>();
        q.eq("account", "lisi");
        Assertions.assertTrue(dao.exists(q));

        q.clear();
        q.isNull("name");
        Assertions.assertEquals(dao.findOne(q).getAccount(), "x");


    }


}
