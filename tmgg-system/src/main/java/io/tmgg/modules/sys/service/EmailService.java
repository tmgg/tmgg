package io.tmgg.modules.sys.service;


import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import io.tmgg.modules.sys.service.SysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.io.File;

@Slf4j
@Service
public class EmailService  {

    @Resource
    SysConfigService sysConfigService;



    public void send(String to,String title, String content, File... files) {
        String from = sysConfigService.getStr("sys.email.from");
        String pass = sysConfigService.getStr("sys.email.pass");
        if(StrUtil.isEmpty(from) || StrUtil.isEmpty(pass)){
            throw new IllegalStateException("邮箱账号或密码未设置,忽略邮件发送");
        }


        MailAccount account = new MailAccount();
        account.setFrom(from);
        account.setPass(pass);

        MailUtil.send(account,to, title, content, false, files);
    }
}
