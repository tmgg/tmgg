package io.tmgg.lang.persistence.test;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import io.tmgg.TestBootApplication;
import io.tmgg.persistence.specification.JpaQuery;
import io.tmgg.mgmt.Author;
import io.tmgg.mgmt.AuthorDao;
import io.tmgg.mgmt.BookDao;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

@Slf4j
@SpringBootTest(classes = TestBootApplication.class)
public class JpaQueryTest {


    public static final String NAME_NOT_EXIST = "名字不存在";

    public static final List<String> ONE_NULL_LIST = ListUtil.of((String) null);

    @Resource
    AuthorDao authorDao;
    @Resource
    BookDao bookDao;

    @BeforeEach
    public void init() {
        bookDao.deleteAllInBatch();
        authorDao.deleteAllInBatch();
        authorDao.save(new Author("zhangsan", "张三", 3));
        authorDao.save(new Author("lisi", "李四", 4));
        authorDao.save(new Author("wangwu", "王五", 5));
        authorDao.save(new Author("x", null, null));
    }

    @Test
    public void test() {
        JpaQuery<Author> q = new JpaQuery<>();
        q.eq("account", "lisi");
        Assertions.assertTrue(authorDao.exists(q));

        q.clear();
        q.isNull("name");
        Assertions.assertEquals(authorDao.findOne(q).getAccount(), "x");


        q.clear();
        q.isNotNull("name");
        Assertions.assertEquals(3L, authorDao.count(q));


        q.clear();
        q.ne("account", "zhangsan");
        Assertions.assertEquals(3L, authorDao.count(q));


        q.clear();
        q.between("age", 3, 5);
        Assertions.assertEquals(3L, authorDao.count(q));

        q.clear();
        q.between("age", 3, 4);
        Assertions.assertEquals(2L, authorDao.count(q));

        q.clear();
        q.notBetween("age", 3, 4);
        Assertions.assertEquals(1L, authorDao.count(q));
    }


    @Test
    public void testIn() {
        JpaQuery<Author> q = new JpaQuery<>();

        q.clear();
        q.in("name", "张三");
        Assertions.assertEquals(1L, authorDao.count(q));

        q.clear();
        q.in("name", NAME_NOT_EXIST);
        Assertions.assertEquals(0L, authorDao.count(q));


        q.clear();
        q.in("name", "张三", "李四", null);
        Assertions.assertEquals(3L, authorDao.count(q));



        q.clear();
        q.in("name", ONE_NULL_LIST);
        Assertions.assertEquals(1L, authorDao.count(q));


        q.clear();
        q.in("name", Collections.emptyList());
        Assertions.assertEquals(0L, authorDao.count(q));

        q.clear();
        q.in("account", "zhangsan", "lisi");
        Assertions.assertEquals(2L, authorDao.count(q));

        q.clear();
        q.in("account", "zhangsan", "lisi", StrUtil.uuid());
        Assertions.assertEquals(2L, authorDao.count(q));

    }

    @Test
    public void testNotIn() {
        JpaQuery<Author> q = new JpaQuery<>();

        q.notIn("name", "张三", "李四");
        Assertions.assertEquals(2L, authorDao.count(q));

        q.clear();
        q.notIn("name", "张三");
        Assertions.assertEquals(3L, authorDao.count(q));

        q.clear();
        q.notIn("name", "张三", "李四", null);
        Assertions.assertEquals(1L, authorDao.count(q));


        q.clear();
        q.notIn("name", "张三",  null);
        Assertions.assertEquals(2L, authorDao.count(q));


        q.clear();
        q.notIn("name",   ONE_NULL_LIST);
        print(q);
        Assertions.assertEquals(3L, authorDao.count(q));
    }


    private void print(JpaQuery<Author> q) {
        List<Author> list = authorDao.findAll(q);
        if(list.isEmpty()){
            System.out.println("查询结果为空");
        }
        list.forEach(System.out::println);
        System.out.println();
        System.out.println();
    }

}
