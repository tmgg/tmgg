package io.tmgg.lang.dao.test;

import io.tmgg.TestBootApplication;
import io.tmgg.mgmt.Author;
import io.tmgg.mgmt.AuthorDao;
import io.tmgg.mgmt.Teacher;
import io.tmgg.mgmt.TeacherDao;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest(classes = TestBootApplication.class)
public class IdTest {
    @Resource
    AuthorDao dao;

    @Resource
    TeacherDao teacherDao;
    @Test
    public void prefixTest() {
        Author d = dao.save(new Author("zhangsan", "张三", 1));
        System.out.println(d);
        Assertions.assertEquals(Author.PREFIX, d.getId().substring(0, Author.PREFIX.length()));

         d = dao.save(new Author("lisi", "李四", 1));
        System.out.println(d);
        Assertions.assertEquals(Author.PREFIX, d.getId().substring(0, Author.PREFIX.length()));

        Teacher t = teacherDao.save(new Teacher("隆老师"));
        System.out.println(t);
         t = teacherDao.save(new Teacher("张老师"));
        System.out.println(t);
    }


}
