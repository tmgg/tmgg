package io.tmgg.web.persistence;


import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.spring.SpringUtil;
import io.tmgg.lang.HttpServletTool;
import io.tmgg.lang.ann.RemarkTool;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import io.tmgg.lang.obj.table.Table;
import io.tmgg.lang.poi.ExcelExportTool;
import io.tmgg.web.WebConstants;
import io.tmgg.web.persistence.specification.JpaQuery;
import jakarta.persistence.Transient;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public abstract class BaseService<T extends PersistEntity> {


    @Autowired
    protected BaseDao<T> baseDao;


    public static String getExportType() {
        HttpServletRequest request = HttpServletTool.getRequest();
        Assert.notNull(request, "request不能为空");

        return request.getHeader(WebConstants.HEADER_EXPORT_TYPE);
    }

    /**
     * 自定渲染，vo的情况
     */
    public <T> AjaxResult autoRender(Page<T> page, Class<T> cls) throws Exception {
        String exportType = getExportType();
        if (exportType == null) {
            return AjaxResult.ok().data(page);
        }

        String fileName = RemarkTool.getRemark(cls);
        if(fileName == null){
            fileName = cls.getSimpleName();
        }
        fileName = fileName + "_" + DateUtil.format(new Date(), DatePattern.PURE_DATETIME_PATTERN);


        // 分页数据转换下
        Table<T> tableData = Table.of(page.getContent(), cls);

        if (exportType.equals("EXCEL")) {
            ExcelExportTool.exportTable(fileName  +".xlsx", tableData);
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
        Sort defaultSort = Sort.by(Sort.Direction.DESC, "createTime");
        return this.findOptionList(labelFn, null, defaultSort);
    }

    /**
     * @param q
     * @param labelFn
     * @return
     * @deprecated 统一使用labelFn为第一个参数
     */
    @Deprecated
    public List<Option> findOptionList(JpaQuery<T> q, Function<T, String> labelFn) {
        Sort defaultSort = Sort.by(Sort.Direction.DESC, "createTime");
        return this.findOptionList(labelFn, q, defaultSort);
    }

    public List<Option> findOptionList(Function<T, String> labelFn, JpaQuery<T> q) {
        Sort defaultSort = Sort.by(Sort.Direction.DESC, "createTime");
        return this.findOptionList(labelFn, q, defaultSort);
    }

    public List<Option> findOptionList(Function<T, String> labelFn, JpaQuery<T> q, Sort sort) {
        List<T> list = this.findAll(q, sort);
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


    /**
     * 更新时，指定字段更新
     * 防止了全字段更新，以免有些字段非前端输入的情况
     *
     * @param input
     * @param updateKeys
     * @return
     * @throws Exception
     */
    @Transactional
    public T saveOrUpdate(T input, List<String> updateKeys) throws Exception {
        String id = input.getId();
        if (id == null) {
            return baseDao.persist(input);
        }

        baseDao.updateField(input, updateKeys);
        return baseDao.findById(id);
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

    public String[] getSearchableFields() {
        Class<T> cls = getEntityClass();
        Field[] fs = cls.getDeclaredFields();
        List<String> fields = new ArrayList<>();
        for (Field f : fs) {
            if (f.getType().equals(String.class)
                && !Modifier.isStatic(f.getModifiers())
                && !f.isAnnotationPresent(Transient.class) && !f.isAnnotationPresent(org.springframework.data.annotation.Transient.class)) {
                String name = f.getName();
                fields.add(name);
            }
        }

        return fields.toArray(String[]::new);
    }

    public String[] getFields() {
        Class<T> cls = getEntityClass();
        Field[] fs = cls.getDeclaredFields();
        List<String> fields = new ArrayList<>();
        for (Field f : fs) {
            if (!Modifier.isStatic(f.getModifiers())
                && !f.isAnnotationPresent(Transient.class) && !f.isAnnotationPresent(org.springframework.data.annotation.Transient.class)) {
                String name = f.getName();
                fields.add(name);
            }
        }

        return fields.toArray(String[]::new);
    }
}
