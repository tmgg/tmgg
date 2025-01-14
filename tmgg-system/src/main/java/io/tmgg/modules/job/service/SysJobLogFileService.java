package io.tmgg.modules.job.service;

import cn.hutool.core.io.IoUtil;
import io.tmgg.modules.job.dao.SysJobLogDao;
import io.tmgg.modules.job.entity.SysJobLog;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.*;

@Slf4j
@Service
public class SysJobLogFileService {


    @Resource
    SysJobLogDao sysJobLogDao;


    @Value("${logging.file.path}")
    String logPath;




    public void read(String jogLogId, PrintWriter printWriter) throws IOException {
        SysJobLog sysJobLog = sysJobLogDao.findOne(jogLogId);

        String filename  = logPath + "/jobs/" + sysJobLog.getSysJob().getJobClass() + "/" + jogLogId + ".log";

        File file = new File(filename);

        if(!file.exists()){
            printWriter.println("文件不存在:" + file.getAbsolutePath());
            return;
        }

        FileInputStream is = new FileInputStream(file);
        IOUtils.copy(is, printWriter);
        is.close();

    }





}
