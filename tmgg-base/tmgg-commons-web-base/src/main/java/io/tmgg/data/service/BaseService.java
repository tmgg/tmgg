package io.tmgg.data.service;


import io.tmgg.data.domain.PersistEntity;
import io.tmgg.data.query.JpaQuery;
import io.tmgg.data.repository.BaseDao;
import io.tmgg.dto.Option;
import jakarta.persistence.Transient;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public abstract class BaseService<T extends PersistEntity> {


    @Delegate
    @Autowired
    protected BaseDao<T> baseDao;

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
    public T saveOrUpdateByRequest(T input, List<String> updateKeys) throws Exception {
        String id = input.getId();
        if (id == null) {
            return baseDao.persist(input);
        }

        baseDao.updateField(input, updateKeys);
        return baseDao.findById(id);
    }


    @Transactional
    public void deleteByRequest(String id) {
        this.deleteById(id);
    }

    public Page<T> findAllByRequest(JpaQuery<T> q, Pageable pageable) {
        return this.findAll(q, pageable);
    }

    public Page<T> findOneByRequest(String id) {
        return this.findOneByRequest(id);
    }


    public <B> Page<B> convertDto(Page<T> pageA, Converter<T, B> converter) {
        List<B> listB = new ArrayList<>();
        for (T a : pageA) {
            B b = converter.convert(a);
            listB.add(b);
        }

        PageImpl<B> page = new PageImpl<>(listB, pageA.getPageable(), pageA.getTotalElements());
        return page;
    }





    public void checkUnique(String id, String field, String value, String errMsg) {
        boolean result = this.isFieldUnique(id, field, value);
        Assert.state(result, errMsg);
    }


    public String[] getSearchableFields() {
        Class<T> cls = getDomainClass();
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
        Class<T> cls = getDomainClass();
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

    public List<Option> findOptionList(Function<T, String> labelFn) {
        Sort defaultSort = Sort.by(Sort.Direction.DESC, "createTime");
        return this.findOptionList(labelFn, null, defaultSort);
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


}
