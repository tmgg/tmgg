package io.tmgg.sys.msg;


import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import io.tmgg.sys.consts.dao.SysConfigDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class EmailService  {

    @Resource
    SysConfigDao sysConfigDao;

    public void send(String to,String title, String content) {
        String from = sysConfigDao.findValueByCode("EMAIL-FROM");
        String pass = sysConfigDao.findValueByCode("EMAIL-PASS");
        if(StrUtil.isEmpty(from) || StrUtil.isEmpty(pass)){
            log.info("邮箱账号或密码未设置,忽略邮件发送 {}", title );
            return;
        }
        MailAccount account = new MailAccount();
        account.setFrom(from);
        account.setPass(pass);

        MailUtil.send(account,to, title, content, false);
    }
}
