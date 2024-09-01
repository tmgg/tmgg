
package io.tmgg.sys.service;

import io.tmgg.sys.dao.SysDictDao;
import io.tmgg.sys.entity.SysDict;
import io.tmgg.sys.entity.SysDictItem;
import io.tmgg.web.enums.CommonStatus;
import io.tmgg.lang.obj.Option;
import io.tmgg.lang.dao.BaseService;
import io.tmgg.lang.dao.specification.JpaQuery;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Service
public class SysDictDataService extends BaseService<SysDictItem> {




}
