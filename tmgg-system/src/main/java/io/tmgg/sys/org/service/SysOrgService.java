package io.tmgg.sys.org.service;

import io.tmgg.lang.dao.BaseService;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.sys.org.dao.SysOrgDao;
import io.tmgg.sys.org.entity.SysOrg;
import io.tmgg.sys.org.enums.OrgType;
import io.tmgg.web.enums.CommonStatus;
import io.tmgg.web.perm.Subject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service
@CacheConfig(cacheNames = "sys_org")
public class SysOrgService extends BaseService<SysOrg> {

    @Resource
    private SysOrgDao dao;


    @Override
    public void deleteById(String id) {
        JpaQuery<SysOrg> query = new JpaQuery<>();
        query.eq(SysOrg.Fields.pid, id);

        long count = dao.count(query);
        Assert.state(count == 0, "请先删除子节点");

        dao.deleteById(id);
    }

    /**
     * 查早所有正常的机构
     *
     *
     */
    public List<SysOrg> findAllValid() {
        return dao.findAllValid();
    }


    /**
     * @param subject
     * @param type
     *
     */
    public List<SysOrg> findByLoginUser(Subject subject, OrgType type, boolean showAll) {
        JpaQuery<SysOrg> q = new JpaQuery<>();

        // 如果不显示全部，则只显示启用的
        if (!showAll) {
            q.eq(SysOrg.Fields.status, CommonStatus.ENABLE);
        }

        List<SysOrg> list = dao.findAll(q, Sort.by(SysOrg.Fields.type, SysOrg.Fields.seq));

        // 权限过滤
        list = list.stream().filter(t -> subject.hasOrgPermission(t.getId())).collect(Collectors.toList());


        if (type != null) {
            // 过滤掉子分支， 例如查询公司，则显示公司，不显示部门
            list = list.stream().filter(o -> o.getType().ordinal() <= type.ordinal()).collect(Collectors.toList());


            // 如果只有一个父节点， 则隐藏该父节点（方便选择）
            long diff = list.stream().filter(o -> o.getType() != type).count();
            if (diff == 1) {
                list = list.stream().filter(o -> o.getType() == type).collect(Collectors.toList());
            }
        }


        return list;
    }

    public Map<String, SysOrg> dict() {
        return dao.dict();
    }

    public String getNameById(String id) {
        return dao.getNameById(id);
    }


    @Transactional
    public SysOrg saveOrUpdate(SysOrg input) {
        boolean isNew = input.isNew();

        if (!isNew) {
            Assert.state(!input.getId().equals(input.getPid()), "父节点不能和本节点一致，请重新选择父节点");
            List<String> childIdListById = dao.findChildIdListById(input.getId());
            Assert.state(!childIdListById.contains(input.getId()), "父节点不能为本节点的子节点，请重新选择父节点");
        }
        return dao.save(input);
    }


    /**
     * 获得叶子节点
     * @param orgs
     *
     */
    public Collection<SysOrg> getLeafs(Collection<SysOrg> orgs) {
        return orgs.stream().filter(o -> dao.checkIsLeaf(o.getId())).collect(Collectors.toList());
    }

    public Collection<String> getLeafIds(Collection<String> orgs) {
        return orgs.stream().filter(orgId -> dao.checkIsLeaf(orgId)).collect(Collectors.toList());
    }

    /**
     * 根据节点id获取所有父节点id集合，不包含自己
     *
 *
     */
    private List<String> getParentIdListById(String id) {
        return dao.getParentIdListById(id);
    }

    public List<String> findChildIdListById(String id) {
        return dao.findChildIdListById(id);
    }

    /**
     * 直接下级公司
     *
     * @param id
     *
     */
    public List<SysOrg> findDirectChildUnit(String id) {
        return dao.findDirectChildUnit(id, null);
    }

    /**
     * 直接下级公司
     *
     * @param id
     *
     */
    public List<SysOrg> findDirectChildUnit(String id, CommonStatus commonStatus) {
        return dao.findDirectChildUnit(id, commonStatus);
    }


    public List<String> findDirectChildUnitIdArr(String id) {
        return dao.findDirectChildUnitId(id);
    }


    public List<SysOrg> findByType(OrgType type) {
        JpaQuery<SysOrg> q = new JpaQuery<>();

        q.eq(SysOrg.Fields.status, CommonStatus.ENABLE);
        q.eq(SysOrg.Fields.type, type);

        return this.findAll(q, Sort.by(SysOrg.Fields.seq));
    }


    public List<SysOrg> findByTypeAndLevel(OrgType orgType, int orgLevel) {
        JpaQuery<SysOrg> q = new JpaQuery<>();
        q.eq(SysOrg.Fields.status, CommonStatus.ENABLE);
        q.eq(SysOrg.Fields.type, orgType);

        List<SysOrg> all = this.findAll(q, Sort.by(SysOrg.Fields.seq));

        return all.stream().filter(o -> dao.findLevelById(o.getId()) == orgLevel).collect(Collectors.toList());
    }


    public List<SysOrg> findByNameOrShortName(String name) {
        JpaQuery<SysOrg> query = new JpaQuery<>();

        query.or(q -> {
            q.eq(SysOrg.Fields.name, name);
            q.eq(SysOrg.Fields.shortName, name);
        });

        return this.findAll(query, Sort.by(SysOrg.Fields.seq));

    }

    /**
     * 组织机构分一般分部门和公司，如果orgId属于部门，则返回该部门对于的公司
     *
     * @param orgId
     *
     */
    public SysOrg findUnitByOrgId(String orgId) {
        SysOrg org = dao.findOne(orgId);

        return dao.findUnit(org);
    }


    @Transactional
    public void toggleAllStatus(String id, boolean enabled) {
        List<String> ids = dao.findChildIdListWithSelfById(id);
        List<SysOrg> all = dao.findAllById(ids);
        for (SysOrg sysOrg : all) {
            sysOrg.setStatus(enabled ? CommonStatus.ENABLE : CommonStatus.DISABLE);
            dao.save(sysOrg);
        }
    }


    public SysOrg findParentUnit(SysOrg org) {
        return dao.findParentUnit(org);
    }
}
