package io.tmgg.mgmt;

import cn.hutool.core.util.RandomUtil;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StudentController {

    @Resource
    StudentDao studentDao;

    @GetMapping("add")
    public Student add(){
        String s = RandomUtil.randomString(5);
        Student student = new Student("stu"+s, "学生"+s, 5);
        return studentDao.save(student);
    }
}
