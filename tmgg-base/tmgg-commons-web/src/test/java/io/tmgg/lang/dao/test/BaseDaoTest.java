package io.tmgg.lang.dao.test;

import cn.hutool.core.util.StrUtil;
import io.tmgg.TestBootApplication;
import io.tmgg.lang.dao.StatField;
import io.tmgg.lang.dao.StatType;
import io.tmgg.lang.dao.specification.ExpressionTool;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.mgmt.Author;
import io.tmgg.mgmt.AuthorDao;
import io.tmgg.mgmt.Book;
import io.tmgg.mgmt.BookDao;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootTest(classes = TestBootApplication.class)
public class BaseDaoTest {


    // 由于base到是个抽象对象，这里以DbCache为例
    @Resource
    AuthorDao dao;

    @Resource
    BookDao bookDao;

    @Test
    public void test() {
        dao.deleteAllInBatch();
        Author d = dao.save(new Author("zhangsan", "张三", 1));


        Assertions.assertEquals(1L, dao.count());
        Assertions.assertTrue(dao.existsById(d.getId()));
        Assertions.assertFalse(dao.existsById(StrUtil.uuid()));


        log.info("existsById传入空会抛异常");
        Assertions.assertThrows(Exception.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                dao.existsById(null);
            }
        });
        Author d2 = dao.save(new Author("lisi", "李四", 1));



        Assertions.assertEquals(2L, dao.count());


        JpaQuery<Author> q = new JpaQuery<>();
        q.eq("account","lisi");
        Assertions.assertEquals(1L, dao.count(q));

        dao.delete(q);
        Assertions.assertEquals(0L, dao.count(q));
        Assertions.assertEquals(1L, dao.count());
    }


    @Test
    public void groupSum() {
        bookDao.save(new Book("道德经","2025-01-01", 1,1));
        bookDao.save(new Book("论语","2025-01-02", 2,2));
        bookDao.save(new Book("论语","2025-01-02", 3,3));

        JpaQuery<Book> q = new JpaQuery<>();
        q.eq("name","论语");

        List<Map> maps = bookDao.groupStats(q, "name", new StatField("saleCount", StatType.SUM), new StatField( "favCount",StatType.SUM));
        System.out.println(maps);
        Assertions.assertEquals(1, maps.size());
        Assertions.assertEquals(maps.get(0).get("saleCount"), 5);




    }


}
