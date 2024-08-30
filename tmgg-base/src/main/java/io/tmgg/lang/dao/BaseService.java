package io.tmgg.lang.dao;


import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.dao.specification.Selector;
import io.tmgg.lang.obj.Option;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class BaseService<T extends Persistable<String>> {

    @Autowired
    protected BaseDao<T> baseDao;



    public Object[] findAggregate(Specification<T> spec, Selector selector) {
        return baseDao.findAggregate(spec, selector);
    }


    public boolean isFieldUnique(String id, String fieldName, Object value) {
        return baseDao.isFieldUnique(id, fieldName, value);
    }


    public List<Option> findOptionList(String searchText, String[] selected, Pageable pageable, String searchField, Function<T, String> labelFn) {
        JpaQuery<T> c = new JpaQuery<>();
        return findOptionList(c, searchText, selected, pageable, searchField, labelFn);
    }

    public List<Option> findOptionList(JpaQuery<T> query, String searchText, String[] selected, Pageable pageable, String searchField, Function<T, String> labelFn) {
        query.like(searchField, searchText);


        Page<T> all = this.findAll(query, pageable);
        Collection<T> roles = new LinkedHashSet<>();
        if (selected != null && selected.length > 0) {
            List<T> selectedList = this.findAllById(selected);
            roles.addAll(selectedList);
            roles.addAll(all.getContent());
        } else {
            roles = all.getContent();
        }
        return roles.stream().map(r -> new Option(String.valueOf(r.getId()), labelFn.apply(r))).collect(Collectors.toList());
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


    public T findTop1(Specification<T> spec, Sort sort) {
        return baseDao.findTop1(spec, sort);
    }


    public T findOne(Specification<T> filter) {
        return baseDao.findOne(filter);
    }


    public Page<T> findAll(Pageable pageable) {
        return baseDao.findAll(pageable);
    }


    public Page<T> findAll(Specification<T> filter, Pageable pageable) {
        return baseDao.findAll(filter, pageable);
    }


    public Page<T> findAll(Specification<T> filter, Pageable pageable, Sort sort) {
        return baseDao.findAll(filter, pageable, sort);
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
        JpaQuery query = new JpaQuery<>().likeExample(example);
        return baseDao.findAll(query, pageable);
    }

    public JpaQuery<T> getQueryByExampleLike(T example) {
        return new JpaQuery<>().likeExample(example);
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
        boolean isNew = input.isNew();
        T old = null;
        if (!isNew) {
            old = baseDao.findOne(input);
        }

        if (old != null) {
            BeanUtil.copyProperties(input, old, CopyOptions.create().setIgnoreProperties(BaseEntity.BASE_ENTITY_FIELDS));
            // 由于加了Transactional注解，会自动修改变化的属性
            return old;
        }

        return baseDao.save(input);
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


    public T findOne(T t) {
        return baseDao.findOne(t);
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
            if(e.getValue() != null){
                query.like(e.getKey(), e.getValue());

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
