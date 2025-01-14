package io.tmgg.modules.job.dao;

import cn.hutool.core.io.FileUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 任务日志文本的存储
 */
@Slf4j
@Service
public class SysJobTextDao {


    @Resource
    SysJobLogDao sysJobLogDao;


    @Value("${logging.file.path}")
    String logPath;


    public void read(String jogLogId, PrintWriter printWriter) throws IOException {
        File file = getFile(jogLogId);


        if (!file.exists()) {
            printWriter.println("文件不存在:" + file.getAbsolutePath());
            return;
        }

        FileInputStream is = new FileInputStream(file);
        IOUtils.copy(is, printWriter);
        is.close();
    }

    public void delete(String jobLogId) {
        File file = getFile(jobLogId);
        FileUtil.del(file);
    }

    private File getFile(String jogLogId) {
        String filename = logPath + "/jobs/" + jogLogId + ".log";
        return new File(filename);
    }

}
