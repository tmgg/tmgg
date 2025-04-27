package io.tmgg.lang.dao.test;

import io.tmgg.TestBootApplication;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.mgmt.Author;
import io.tmgg.mgmt.AuthorDao;
import io.tmgg.mgmt.Book;
import io.tmgg.mgmt.BookDao;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Path;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

@Slf4j
@SpringBootTest(classes = TestBootApplication.class)
public class JpaQueryDeepTest {


    @Resource
    AuthorDao dao;

    @Resource
    BookDao bookDao;
    private Author ls;
    private Author author;
    private Book book;

    @BeforeEach
    public void init() {
        bookDao.deleteAllInBatch();
        dao.deleteAllInBatch();


        this.author = dao.save(new Author("doudou", "豆豆", 3));
        this.ls = dao.save(new Author("cxq", "曹雪芹", 4));

        // 宠物
        this.book = bookDao.save(new Book("遥远的救世主", author));
        bookDao.save(new Book("天幕红尘", author));
        bookDao.save(new Book("红楼梦", ls));
    }
    @Test
    public void queryBook() {
        log.info("查询书籍, 查询豆豆写的书");

        JpaQuery<Book> q = new JpaQuery<>();
        q.eq("author", author);

        List<Book> list = bookDao.findAll(q);
        Assertions.assertEquals(2, list.size());
        Assertions.assertEquals("豆豆", list.get(0).getAuthor().getName());
    }

    @Test
    public void queryAuthorByBookName() {
        log.info("查询作者列表，条件是写了《遥远的救世主》");

        JpaQuery<Author> q = new JpaQuery<>();
        q.eq("books.name", "遥远的救世主");
        List<Author> list = dao.findAll(q);
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals("豆豆", list.get(0).getName());
    }

    @Test
    public void queryAuthorByBookId() {
        log.info("查询作者列表，条件是写了《遥远的救世主》");

        JpaQuery<Author> q = new JpaQuery<>();
        q.eq("books.id", book.getId());
        List<Author> list = dao.findAll(q);
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals("豆豆", list.get(0).getName());
    }

    @Test
    public void queryAuthorByBook() {
        log.info("查询作者列表，条件是写了《遥远的救世主》");

        JpaQuery<Author> q = new JpaQuery<>();
        q.eq("books", book);
        List<Author> list = dao.findAll(q);
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals("豆豆", list.get(0).getName());
    }


    @Test
    public void queryBookByAuthorName() {
        log.info("查询书籍, 查询豆豆写的书");

        JpaQuery<Book> q = new JpaQuery<>();
        q.eq("author.name", "豆豆");

        List<Book> list = bookDao.findAll(q);
        Assertions.assertEquals(2, list.size());
        Assertions.assertEquals("豆豆", list.get(0).getAuthor().getName());
    }
}
