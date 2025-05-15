package io.tmgg.modules.asset.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.ArrayUtil;
import io.tmgg.web.persistence.BaseEntity;
import io.tmgg.web.persistence.BaseService;
import io.tmgg.web.persistence.specification.JpaQuery;
import io.tmgg.modules.asset.dao.SysAssetDao;
import io.tmgg.modules.asset.entity.SysAsset;
import io.tmgg.modules.asset.entity.SysAssetType;
import io.tmgg.modules.sys.service.SysFileService;
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

    public List<SysAsset> findAll(SysAssetType sysAssetType) {
        JpaQuery<SysAsset> q = new JpaQuery<>();
        q.eq(SysAsset.Fields.type, sysAssetType);
        return sysAssetDao.findAll(q);
    }

    public void preview(String code, HttpServletResponse resp) throws Exception {
        SysAsset a = sysAssetDao.findByCode(code);
        String content = a.getContent();
        SysAssetType type = a.getType();

        switch (type) {
            case DIR:
                return;
            case IMAGE:
            case VIDEO:
                sysFileService.preview(content, resp);
                return;
            case TEXT:
                resp.setContentType("text/html;charset=utf-8");
                resp.getWriter().write(content);
                return;
        }


    }
}

