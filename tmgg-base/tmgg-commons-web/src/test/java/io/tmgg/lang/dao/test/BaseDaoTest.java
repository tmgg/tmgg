package io.tmgg.lang.dao.test;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import io.tmgg.TestBootApplication;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.web.db.DbCache;
import io.tmgg.web.db.DbCacheDao;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.jpa.repository.JpaRepository;

@Slf4j
@SpringBootTest(classes = TestBootApplication.class)
public class BaseDaoTest {


    // 由于base到是个抽象对象，这里以DbCache为例
    @Resource
    DbCacheDao dao;

    @Test
    public void test() {
        DbCache d = new DbCache();
        d.setCode("zhangsan");
        d.setValue("张三");
        dao.save(d);


        Assertions.assertEquals(dao.count(), 1L);
        Assertions.assertTrue(dao.existsById(d.getId()));
        Assertions.assertFalse(dao.existsById(StrUtil.uuid()));


        log.info("existsById传入空会抛异常");
        Assertions.assertThrows(Exception.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                dao.existsById(null);
            }
        });


        DbCache d2 = new DbCache();
        d2.setCode("lisi");
        d2.setValue("李四");
        dao.save(d2);
        Assertions.assertEquals(dao.count(), 2L);


        JpaQuery<DbCache> q = new JpaQuery<>();
        q.eq("code","lisi");
        Assertions.assertEquals(dao.count(q), 1L);

        dao.delete(q);
        Assertions.assertEquals(dao.count(q), 0L);
        Assertions.assertEquals(dao.count(), 1L);
    }



}
