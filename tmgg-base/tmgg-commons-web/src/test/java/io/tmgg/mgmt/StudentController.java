package io.tmgg.mgmt;

import cn.hutool.core.util.RandomUtil;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StudentController {

    @Resource
    AuthorDao authorDao;

    @GetMapping("add")
    public Author add(){
        String s = RandomUtil.randomString(5);
        Author author = new Author("stu" + s, "学生" + s, 5);
        return authorDao.save(author);
    }
}
