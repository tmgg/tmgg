package io.tmgg.modules.asset.service;

import io.tmgg.modules.asset.dao.SysAssetDao;
import io.tmgg.modules.asset.entity.SysAsset;
import io.tmgg.modules.system.service.SysFileService;
import io.tmgg.web.persistence.BaseService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;

@Service
public class SysAssetService extends BaseService<SysAsset> {

    @Resource
    private SysAssetDao sysAssetDao;

    @Resource
    private SysFileService sysFileService;


    @Transactional
    @Override
    @SneakyThrows
    public void deleteById(String id) {
        SysAsset a = sysAssetDao.findOne(id);
        if(a.getType() ==1){
            if(a.getContent() != null){
                sysFileService.deleteById(a.getContent());
            }
        }
        super.deleteById(id);
    }

    @Transactional
    public void saveContent(SysAsset param) {
        SysAsset db = sysAssetDao.findOne(param.getId());
        db.setContent(param.getContent());
    }



    public void preview(String name, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        SysAsset a = sysAssetDao.findByName(name);
        String content = a.getContent();
       Integer type = a.getType();

        if (type==0) {
            resp.setContentType("text/html;charset=utf-8");
            PrintWriter writer = resp.getWriter();
            writer.write(content);
            writer.flush();
            writer.close();
            return;
        }
        sysFileService.preview(content, req, resp);
        return;

    }
}

