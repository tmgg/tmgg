package io.tmgg.lang.dao.test;

import cn.hutool.core.util.StrUtil;
import io.tmgg.TestBootApplication;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.mgmt.Author;
import io.tmgg.mgmt.AuthorDao;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest(classes = TestBootApplication.class)
public class BaseDaoTest {


    // 由于base到是个抽象对象，这里以DbCache为例
    @Resource
    AuthorDao dao;

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



}
