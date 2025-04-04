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
public class IdTest {
    @Resource
    StudentDao dao;
    @Test
    public void prefixTest() {
        Student d = dao.save(new Student("zhangsan", "张三", 1));
        System.out.println(d);
        Assertions.assertEquals(Student.PREFIX, d.getId().substring(0,Student.PREFIX.length()));
    }


}
