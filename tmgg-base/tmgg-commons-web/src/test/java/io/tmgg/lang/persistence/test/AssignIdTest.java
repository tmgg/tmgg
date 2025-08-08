package io.tmgg.lang.persistence.test;

import io.tmgg.TestBootApplication;
import io.tmgg.mgmt.AuthorDao;
import io.tmgg.mgmt.Teacher;
import io.tmgg.mgmt.TeacherDao;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@SpringBootTest(classes = TestBootApplication.class)
public class AssignIdTest {
    @Resource
    AuthorDao dao;

    @Resource
    TeacherDao teacherDao;

    @Test
    public void prefixTest() {
        Teacher t = new Teacher();
        String id = "001";
        t.setId(id);
        t.setName("张老师");
        teacherDao.save(t);

        Assertions.assertTrue( teacherDao.existsById(id));


        Teacher t2 = teacherDao.findOne(id);
        t2.setName("王老师");

        teacherDao.save(t2);


        System.out.println(teacherDao.findAll());

    }


}
