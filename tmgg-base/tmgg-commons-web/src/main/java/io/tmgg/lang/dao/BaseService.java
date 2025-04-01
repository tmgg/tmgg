package io.tmgg.lang.dao;


import io.tmgg.data.domain.PageExt;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.dao.specification.MultiSelector;
import io.tmgg.lang.dao.specification.Selector;
import io.tmgg.lang.obj.Option;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import io.tmgg.lang.poi.ExcelExportTool;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class BaseService<T extends PersistEntity> {


    @Autowired
    protected BaseDao<T> baseDao;


    /**
     * 聚合统计 参见 @see BaseDao.findAggregate
     * @param spec
     * @param selector
     * @return
     */
    public Object[] findAggregate(Specification<T> spec, MultiSelector selector) {
        return baseDao.findAggregate(spec, selector);
    }

    /**
     *
     * @param spec
     * @param pageable
     * @return
     */
    public PageExt<T> page(Specification<T> spec,Pageable pageable){
        Page<T> page = baseDao.findAll(spec, pageable);
        return PageExt.of(page);
    }

    public void exportExcel(Specification<T> spec, String filename, HttpServletResponse response) throws IOException {
        List<T> list = baseDao.findAll(spec);
        ExcelExportTool.exportBeanList(filename, list, getEntityClass(), response);
    }
    public void exportExcel(Specification<T> spec, String filename, LinkedHashMap<String, Function<T, Object>> columns, HttpServletResponse response) throws IOException {
        List<T> list = baseDao.findAll(spec);
        ExcelExportTool.exportBeanList(filename,  list, columns, response);
    }


    public boolean isFieldUnique(String id, String fieldName, Object value) {
        return baseDao.isFieldUnique(id, fieldName, value);
    }

    public List<Option> findOptionList(Function<T, String> labelFn) {
        List<T> list = this.findAll(Sort.by(Sort.Direction.DESC, "createTime"));
        return list.stream().map(r -> {
            String label = labelFn.apply(r);
            String value = r.getId();
            return Option.builder().label(label).value(value).build();
        }).collect(Collectors.toList());
    }

    public List<Option> findOptionList(JpaQuery<T> q, Function<T, String> labelFn) {
        List<T> list = this.findAll(q, Sort.by(Sort.Direction.DESC, "createTime"));
        return list.stream().map(r -> {
            String label = labelFn.apply(r);
            String value = r.getId();
            return Option.builder().label(label).value(value).build();
        }).collect(Collectors.toList());
    }


    public List<T> findAllById(String[] ids) {
        return baseDao.findAllById(ids);
    }


    public List<T> findAllById(Iterable<String> ids) {
        return baseDao.findAllById(ids);
    }


    public long count() {
        return baseDao.count();
    }

    public boolean exist(String id) {

        return baseDao.exist(id);
    }


    public long count(Specification<T> spec) {
        return baseDao.count(spec);
    }


    public T findOne(String id) {
        return baseDao.findOne(id);
    }

    public T findOne(Specification<T> specification) {
        return baseDao.findOne(specification);
    }


    public T findTop1(Specification<T> spec, Sort sort) {
        return baseDao.findTop1(spec, sort);
    }





    public Page<T> findAll(Pageable pageable) {
        return baseDao.findAll(pageable);
    }


    public Page<T> findAll(Specification<T> specification, Pageable pageable) {
        return baseDao.findAll(specification, pageable);
    }




    public Page<T> findAll(Specification<T> specification, Pageable pageable, Sort sort) {
        return baseDao.findAll(specification, pageable, sort);
    }


    public List<T> findAll() {
        return baseDao.findAll();
    }


    public List<T> findByExampleLike(T t) {
        return baseDao.findByExampleLike(t);
    }


    public List<T> findByExampleLike(T t, Sort sort) {
        JpaQuery<T> c = new JpaQuery<>();
        c.likeExample(t);
        return baseDao.findAll(c, sort);
    }


    public Page<T> findByExampleLike(T example, Pageable pageable) {
        JpaQuery<T> query = new JpaQuery<>();
        query.likeExample(example);
        return baseDao.findAll(query, pageable);
    }

    public JpaQuery<T> getQueryByExampleLike(T example) {
        JpaQuery<T> q = new JpaQuery<>();
        q.likeExample(example);
        return q;
    }

    public Page<T> findAll(Map<String, Object> map, Pageable pageable) {
        return baseDao.findAll(map, pageable);
    }


    public List<T> findAll(Specification<T> filter) {
        return baseDao.findAll(filter);
    }


    public List<T> findAll(Sort sort) {
        return baseDao.findAll(sort);
    }


    public List<T> findAll(Specification<T> c, Sort sort) {
        return baseDao.findAll(c, sort);
    }


    @Transactional
    public void deleteById(String id) {
        Assert.hasText(id, "id不能为空");

        T db = baseDao.findOne(id);
        baseDao.deleteById(id);
    }

    @Transactional
    public void deleteOneWithChildren(String id, String parentProperty) {
        baseDao.deleteWithChildren(id, parentProperty);
    }


    @Transactional
    public void deleteAllWithChildren(List<String> idList, String parentProperty) {
        for (String id : idList) {
            baseDao.deleteWithChildren(id, parentProperty);
        }

    }


    @Transactional
    public void deleteAllWithChildren(String[] idList, String parentProperty) {
        for (String id : idList) {
            baseDao.deleteWithChildren(id, parentProperty);
        }

    }

    public void flush() {
        baseDao.flush();
    }


    @Transactional
    public T save(T input) {
        return baseDao.save(input);
    }


    @Transactional
    public T saveOrUpdate(T input) throws Exception {
        boolean isNew = input.getId() == null;
        if (isNew) {
            return baseDao.save(input);
        }

        T old = baseDao.findOne(input);

        // 复制到原始数据，如果只想更新部分字段，可调整 ignoreProperties
        BeanUtil.copyProperties(input, old, CopyOptions.create().setIgnoreProperties(BaseEntity.BASE_ENTITY_FIELDS));
        return baseDao.save(old);
    }


    public T saveAndFlush(T input) {
        return baseDao.saveAndFlush(input);
    }

    @Transactional
    public List<T> saveAll(List<T> list) {
        return baseDao.saveAll(list);
    }


    @Transactional
    public void deleteAll(List<T> list) {
        for (T t : list) {
            this.deleteById(t.getId());
        }
    }


    @Transactional
    public void deleteAllById(List<String> idList) {
        for (String id : idList) {
            this.deleteById(id);
        }
    }


    @Transactional
    public void deleteAllById(String[] idList) {
        for (String t : idList) {
            if (t != null) {
                this.deleteById(t);
            }
        }
    }





    public void deleteAll() {
        baseDao.deleteAll();
    }


    public Class<T> getEntityClass() {
        return baseDao.getDomainClass();
    }

    public Page<T> findByLike(Pageable pageable, Map<String, Object> param, String searchValue, String[] fields) {
        JpaQuery<T> query = new JpaQuery<>();

        query.like(param);


        if (StrUtil.isNotEmpty(searchValue) && fields != null && fields.length > 0) {
            query.or(q -> {
                for (String f : fields) {
                    q.like(f, searchValue);
                }
            });
        }


        return this.findAll(query, pageable);
    }

    public List<T> findByLike(Map<String, Object> param) {
        JpaQuery<T> query = new JpaQuery<>();

        Set<Map.Entry<String, Object>> entries = param.entrySet();
        for (Map.Entry<String, Object> e : entries) {
            if (e.getValue() != null) {
                query.like(e.getKey(), (String) e.getValue());

            }
        }

        return this.findAll(query);
    }

    @Transactional
    @Deprecated
    public void deleteOne(JpaQuery<T> query) {
        T data = baseDao.findOne(query);
        SpringUtil.getBean(getClass()).deleteById(data.getId());
    }


    public void checkUnique(String id, String field, String value, String errMsg) {
        boolean result = this.isFieldUnique(id, field, value);
        Assert.state(result, errMsg);
    }

    @Transactional
    public void deleteAll(JpaQuery<T> query) {
        List<T> list = baseDao.findAll(query);

        BaseService service = SpringUtil.getBean(getClass());
        for (T t : list) {
            service.deleteById(t.getId());
        }
    }


}
