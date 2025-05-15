package io.tmgg.modules.asset.service;

import io.tmgg.modules.asset.dao.SysAssetDao;
import io.tmgg.modules.asset.entity.SysAsset;
import io.tmgg.modules.sys.service.SysFileService;
import io.tmgg.web.persistence.BaseService;
import io.tmgg.web.persistence.specification.JpaQuery;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysAssetService extends BaseService<SysAsset> {

    @Resource
    private SysAssetDao sysAssetDao;

    @Resource
    private SysFileService sysFileService;




    @Transactional
    public void saveContent(SysAsset param) {
        SysAsset db = sysAssetDao.findOne(param.getId());
        db.setContent(param.getContent());
    }



    public void preview(String name, HttpServletResponse resp) throws Exception {
        SysAsset a = sysAssetDao.findByName(name);
        String content = a.getContent();
       Integer type = a.getType();

        if (type==0) {
            resp.setContentType("text/html;charset=utf-8");
            resp.getWriter().write(content);
            return;
        }
        sysFileService.preview(content, resp);
        return;

    }
}

