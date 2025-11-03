
package io.tmgg.modules.system.dao;

import io.tmgg.data.repository.BaseDao;
import io.tmgg.modules.system.entity.SysFile;
import org.springframework.stereotype.Repository;

@Repository
public class SysFileDao extends BaseDao<SysFile> {

    public SysFile findByTradeNo(String tradeNo) {
        return this.findOne(SysFile.Fields.tradeNo, tradeNo);
    }
}
