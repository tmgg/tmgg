package io.tmgg.lang.dao;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import io.tmgg.data.domain.PageExt;
import io.tmgg.lang.HttpServletTool;
import io.tmgg.lang.ann.Msg;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import io.tmgg.lang.obj.Table;
import io.tmgg.lang.poi.ExcelExportTool;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class BaseService<T extends PersistEntity> {


    @Autowired
    protected BaseDao<T> baseDao;


    /**
     * @param spec
     * @param pageable
     * @return
     */
    public PageExt<T> page(Specification<T> spec, Pageable pageable) {
        Page<T> page = baseDao.findAll(spec, pageable);
        return PageExt.of(page);
    }


    /**
     * 通过注解@Excel导出
     * @param list
     * @param filename
     * @param response
     * @throws IOException
     */
    public void exportExcel(List<T> list, String filename, HttpServletResponse response) throws IOException {
        ExcelExportTool.exportBeanList(filename, list, getEntityClass(), response);
    }

    /**
     * 自定义导出
     * @param table
     * @param filename
     * @param response
     * @throws IOException
     */
    public void exportExcel(Table<T> table, String filename, HttpServletResponse response) throws IOException {
        ExcelExportTool.exportTable(filename, table, response);
    }


    public <D> AjaxResult page(Page<D> page) throws IOException {
        HttpServletRequest request = HttpServletTool.getRequest();
        HttpServletResponse response = HttpServletTool.getResponse();
        String exportExcel = request.getHeader("X-Export-Type");
        if (StrUtil.isEmpty(exportExcel)) {
            return AjaxResult.ok().data(page);
        }

        Type superClass = getClass().getGenericSuperclass();
        Type type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
        Class<D> cls = (Class<D>) type;

        switch (exportExcel) {
            case "Excel":
                Msg msg = cls.getAnnotation(Msg.class);
                String filename = msg != null ? msg.value() : cls.getSimpleName();
                ExcelExportTool.exportBeanList(filename + ".xlsx", page.getContent(), cls, response);
            break;

        }

        return null;
    }


    public boolean isFieldUnique(String id, String fieldName, Object value) {
        JpaQuery<T> q = new JpaQuery<>();
        q.ne("id", id);
        q.eq(fieldName, value);
        return baseDao.exists(q);
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

    public boolean existsById(String id) {
        return baseDao.existsById(id);
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


    public List<T> findAll() {
        return baseDao.findAll();
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
        baseDao.deleteById(id);
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

        T old = baseDao.findById(input.getId());

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


    public Map<String, T> dict(Specification<T> specification) {
        return baseDao.dict(specification);
    }

    public Map<String, T> dict(Specification<T> spec, Function<T, String> keyField) {
        return baseDao.dict(spec, keyField);
    }

    public <V> Map<String, V> dict(Specification<T> spec, Function<T, String> keyField, Function<T, V> valueField) {
        return baseDao.dict(spec, keyField, valueField);
    }

    public T findByField(String key, Object value) {
        JpaQuery<T> q = new JpaQuery<>();
        q.eq(key, value);
        return this.findOne(q);
    }

    public T findByField(String key, Object value, String key2, Object value2) {
        JpaQuery<T> q = new JpaQuery<>();
        q.eq(key, value);
        q.eq(key2, value2);
        return this.findOne(q);
    }

    public List<T> findAllByField(String key, Object value) {
        JpaQuery<T> q = new JpaQuery<>();
        q.eq(key, value);
        return this.findAll(q);
    }

    public List<T> findAllByField(String key, Object value, String key2, Object value2) {
        JpaQuery<T> q = new JpaQuery<>();
        q.eq(key, value);
        q.eq(key2, value2);
        return this.findAll(q);
    }
}
