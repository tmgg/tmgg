package io.tmgg.web.persistence;

import io.tmgg.web.persistence.specification.ExpressionTool;
import io.tmgg.web.persistence.specification.Selector;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;

/**
 * 基础dao
 * <p>
 * BaseDao的查询条件不依赖JpaQuery
 */
@Slf4j
public class BaseDao<T extends PersistEntity> {

    @Getter
    @PersistenceContext
    protected EntityManager entityManager;

    protected JpaEntityInformation<T, ?> entityInformation;

    @Getter
    protected Class<T> domainClass;
    private SimpleJpaRepository<T, String> rep;


    @PostConstruct
    void init() {
        this.domainClass = parseDomainClass();
        this.entityInformation = JpaEntityInformationSupport.getEntityInformation(domainClass, entityManager);
        this.rep = new SimpleJpaRepository<>(domainClass, entityManager);
    }

    @Transactional
    public void deleteById(String id) {
        rep.deleteById(id);
    }


    @Transactional
    public void delete(T entity) {
        rep.delete(entity);
    }

    @Transactional
    public void deleteAllById(Iterable<String> ids) {
        rep.deleteAllById(ids);
    }

    @Transactional
    public void deleteAllById(String[] ids) {
        rep.deleteAllById(List.of(ids));
    }


    @Transactional
    public void deleteAllByIdInBatch(Iterable<String> ids) {
        rep.deleteAllByIdInBatch(ids);
    }

    @Transactional
    public void deleteAll(Iterable<T> entities) {
        rep.deleteAll(entities);
    }

    @Transactional
    public void deleteAllInBatch(Iterable<T> entities) {
        rep.deleteAllInBatch(entities);
    }

    @Transactional
    public void deleteAll() {
        rep.deleteAll();
    }

    @Transactional
    public void deleteAllInBatch() {
        rep.deleteAllInBatch();
    }

    public T findById(String id) {
        return rep.findById(id).orElse(null);
    }

    public T findByIdAndRefresh(String id) {
        T t = this.findById(id);
        this.refresh(t);
        return t;
    }

    /**
     * 将实体刷新，避免从缓存取
     */
    public void refresh(T t) {
        if (t != null) {
            entityManager.refresh(t);
        }
    }

    public boolean existsById(String id) {
        return rep.existsById(id);
    }

    public List<T> findAll() {
        return rep.findAll();
    }

    public List<T> findAllById(Iterable<String> ids) {
        return rep.findAllById(ids);
    }

    public List<T> findAllById(String[] ids) {
        return rep.findAllById(List.of(ids));
    }

    public List<T> findAll(Sort sort) {
        return rep.findAll(sort);
    }

    public Page<T> findAll(Pageable pageable) {
        return rep.findAll(pageable);
    }

    public T findOne(Specification<T> spec) {
        return rep.findOne(spec).orElse(null);

    }

    public List<T> findAll(Specification<T> spec) {
        return rep.findAll(spec);
    }

    public Page<T> findAll(Specification<T> spec, Pageable pageable) {
        return rep.findAll(spec, pageable);
    }

    public List<T> findAll(Specification<T> spec, Sort sort) {
        return rep.findAll(spec, sort);
    }

    public boolean exists(Specification<T> spec) {
        return rep.exists(spec);
    }

    @Transactional
    public long delete(Specification<T> spec) {
        return rep.delete(spec);
    }

    public <R> R findBy(Specification<T> spec, Function<FluentQuery.FetchableFluentQuery<T>, R> queryFunction) {
        return rep.findBy(spec, queryFunction);
    }

    public T findOne(Example<T> example) {
        return rep.findOne(example).orElse(null);
    }

    public long count(Example<T> example) {
        return rep.count(example);

    }

    public boolean exists(Example<T> example) {
        return rep.exists(example);
    }

    public List<T> findAll(Example<T> example) {
        return rep.findAll(example);
    }

    public List<T> findAll(Example<T> example, Sort sort) {
        return rep.findAll(example, sort);
    }

    public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {
        return rep.findAll(example, pageable);
    }

    public <R> R findBy(Example<T> example, Function<FluentQuery.FetchableFluentQuery<T>, R> queryFunction) {
        return rep.findBy(example, queryFunction);
    }

    public long count() {
        return rep.count();

    }

    public long count(Specification<T> spec) {
        return rep.count(spec);
    }

    /**
     * 插入或更新
     *
     * @param entity
     * @return
     */
    @Transactional
    public T save(T entity) {
        Assert.notNull(entity, "Entity must not be null");
        String id = entity.getId();

        if (this.entityInformation.isNew(entity)) {
            this.entityManager.persist(entity);
            return entity;
        }

        // 从3.3升级到3.4后， hibernate也升级了，不能新增是指定id，这里使用customId替换下
        if (!existsById(id)) {
            entity.setCustomId(id);
            entity.setId(null);
            this.entityManager.persist(entity);
            return entity;
        }

        return this.entityManager.merge(entity);
    }

    /**
     * 先判断是否存在，然后再保存
     *
     * @param entity
     * @return
     */
    @Transactional
    public T saveIfAbsent(T entity) {
        String id = entity.getId();
        if (!existsById(id)) {
            entity.setCustomId(id);
            entity.setId(null);
            this.entityManager.persist(entity);
        }

        return entity;
    }

    @Transactional
    public T saveAndFlush(T entity) {
        T result = save(entity);
        flush();

        return result;
    }

    @Transactional
    public List<T> saveAll(Iterable<T> entities) {
        List<T> result = new ArrayList<>();
        for (T entity : entities) {
            result.add(save(entity));
        }

        return result;
    }

    @Transactional
    public List<T> saveAllAndFlush(Iterable<T> entities) {
        List<T> result = saveAll(entities);
        flush();

        return result;
    }


    @Transactional
    public T update(T entity) {
        return entityManager.merge(entity);
    }

    @Transactional
    public T insert(T entity) {
        entityManager.persist(entity);
        return entity;
    }


    @Transactional
    public void flush() {
        entityManager.flush();
    }


    public T findOne(String id) {
        return rep.findById(id).orElse(null);
    }

    public Map<String, T> findKeyed(Iterable<String> ids) {
        List<T> list = this.findAllById(ids);
        Map<String, T> map = new HashMap<>();
        for (T t : list) {
            map.put(t.getId(), t);
        }
        return map;
    }


    public T findTop1(Specification<T> c, Sort sort) {
        PageRequest pageRequest = PageRequest.of(0, 1, sort);

        Page<T> all = this.findAll(c, pageRequest);
        if (all.getTotalElements() > 0) {
            return all.getContent().get(0);
        }
        return null;
    }


    public List<T> findTop(int topSize, Specification<T> c, Sort sort) {
        PageRequest pageRequest = PageRequest.of(0, topSize, sort);

        Page<T> all = this.findAll(c, pageRequest);
        if (all.getTotalElements() > 0) {
            return all.getContent();
        }
        return Collections.emptyList();
    }

    /***
     * 查询字段列表
     * 如姓名列表
     * @param fieldName
     * @param c
     * @return
     * @param <R>
     */
    public <R> List<R> findField(String fieldName, Specification<T> c) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery query = builder.createQuery();
        Root root = query.from(domainClass);

        Path path = root.get(fieldName);
        Predicate predicate = c.toPredicate(root, query, builder);

        query.select(path).where(predicate);

        return (List<R>) entityManager.createQuery(query).getResultList();
    }


    /**
     * 查询一个单值
     * 如select sum(age) from user
     *
     * @param spec
     * @param selector
     * @return
     */
    public Object selectSingle(Specification<T> spec, Selector selector) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object> query = builder.createQuery(Object.class);
        Root<T> root = query.from(domainClass);

        List<Selection<?>> selections = selector.select(builder, root);
        Assert.state(selections.size() == 1, "selection的个数应为1");
        Selection<?> selection = selections.get(0);

        query.select(selection).where(spec.toPredicate(root, query, builder));

        return entityManager.createQuery(query).getSingleResult();
    }

    /**
     * 查询多个值，但只返回一行，常用于统计
     * 如 select sum(age), count(*) from user
     *
     * @param spec
     * @param selector
     * @return
     */
    public Object[] select(Specification<T> spec, Selector selector) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object> query = builder.createQuery(Object.class);
        Root<T> root = query.from(domainClass);

        List<Selection<?>> selections = selector.select(builder, root);
        Assert.state(selections.size() > 1, "selections需多个");


        Predicate predicate = spec.toPredicate(root, query, builder);
        query.multiselect(selections).where(predicate);

        return (Object[]) entityManager.createQuery(query).getSingleResult();
    }

    /**
     * 按分组字段统计结果
     *
     * @param groupField 分组字段（支持嵌套路径，如"user.id"）
     * @param selector   聚合选择器
     * @return 分组统计结果（每行格式：Map{"groupField": value, "stat1": value1, ...}）
     */
    @Deprecated
    public List<Map> select(Specification<T> spec, String groupField, Selector selector) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Map> query = builder.createQuery(Map.class);
        Root<T> root = query.from(domainClass);

        Expression group = ExpressionTool.getExpression(groupField, root); //分组字段

        List<Selection<?>> selections = new ArrayList<>();
        selections.add(group.alias(groupField));
        selections.addAll(selector.select(builder, root));

        Predicate predicate = spec.toPredicate(root, query, builder);
        query.multiselect(selections).where(predicate).groupBy(group);

        return entityManager.createQuery(query).getResultList();
    }


    /**
     * 分组统计
     *
     * @return
     */
    public List<Map> groupStats(Specification<T> spec, String[] groupFields, StatField... statFields) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Map> query = builder.createQuery(Map.class);
        Root<T> root = query.from(domainClass);
        List<Selection<?>> selections = new ArrayList<>();


        Expression[] groups = new Expression[groupFields.length];
        for (int i = 0; i < groupFields.length; i++) {
            String groupField = groupFields[i];
            Expression group = ExpressionTool.getExpression(groupField, root); //分组字段
            groups[i] = group;
            group.alias(groupField);
            selections.add(group);
        }

        for (StatField statField : statFields) {
            String fieldName = statField.getName();
            Path<Number> f = root.get(fieldName);
            Expression<?> statExpr = null;
            switch (statField.getType()) {
                case SUM:
                    statExpr = builder.sum(f);
                    break;
                case COUNT:
                    statExpr = builder.count(f);
                    break;
                case AVG:
                    statExpr = builder.avg(f);
                    break;
                case MIN:
                    statExpr = (builder.min(f));
                    break;
                case MAX:
                    statExpr = (builder.max(f));
                    break;
                default:
                    throw new IllegalStateException("not support stat type " + statField.getType());

            }
            selections.add(statExpr.alias(fieldName));
        }

        Predicate predicate = spec.toPredicate(root, query, builder);
        query.multiselect(selections).where(predicate).groupBy(groups);

        return entityManager.createQuery(query).getResultList();
    }

    public List<Map> groupStats(Specification<T> spec, String groupFields, StatField... statFields) {
        return this.groupStats(spec, new String[]{groupFields}, statFields);
    }


    /**
     * 分株统计数量
     *
     * @param q
     * @param groupField
     * @return
     */
    public Map<String, Long> count(Specification<T> q, String groupField) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object> query = builder.createQuery(Object.class);
        Root<T> root = query.from(domainClass);

        Expression group = ExpressionTool.getExpression(groupField, root); // 支持 . 分割， 如 user.id

        Predicate predicate = q.toPredicate(root, query, builder);
        query.multiselect(group, builder.count(root)).where(predicate).groupBy(group);

        List<Object> resultList = entityManager.createQuery(query).getResultList();

        // 组装数据结构
        Map<String, Long> map = new HashMap<>();
        for (Object row : resultList) {
            Object[] rowArr = (Object[]) row;
            String groupValue = (String) rowArr[0];
            Number count = (Number) rowArr[1];
            if (count != null) {
                map.put(groupValue, count.longValue());
            }
        }
        return map;
    }

    /**
     * 将查找接口转换为map， key为id，value为对象
     *
     * @param spec
     * @return
     */
    public Map<String, T> dict(Specification<T> spec) {
        List<T> list = this.findAll(spec);
        Map<String, T> map = new HashMap<>();
        for (T t : list) {
            map.put(t.getId(), t);
        }
        return map;
    }

    public Map<String, T> dict(Specification<T> spec, Function<T, String> keyField) {
        List<T> list = this.findAll(spec);
        Map<String, T> map = new HashMap<>();
        for (T t : list) {
            String key = keyField.apply(t);
            if (key != null) {
                map.put(key, t);
            }
        }
        return map;
    }

    /**
     * 将查询结果的两个字段组装成map
     *
     * @param spec
     * @param keyField
     * @param valueField
     * @param <V>
     * @return
     */
    public <V> Map<String, V> dict(Specification<T> spec, Function<T, String> keyField, Function<T, V> valueField) {
        List<T> list = this.findAll(spec);
        Map<String, V> map = new HashMap<>();
        for (T t : list) {
            String key = keyField.apply(t);
            if (key != null) {
                map.put(key, valueField.apply(t));
            }
        }
        return map;
    }

    private Class<T> parseDomainClass() {
        Type type = getClass().getGenericSuperclass();

        //解决多层继承拿泛型类型   //BaseBaseService<User> <- UserService <- PassportService
        while (!(type instanceof ParameterizedType)) {
            type = ((Class<?>) type).getGenericSuperclass();
            if (type == null || "java.lang.Object".equals(type.getClass().getName())) {
                break;
            }
        }

        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] genericTypes = parameterizedType.getActualTypeArguments();
            return (Class<T>) genericTypes[0];
        }
        throw new IllegalStateException("解析DomainClass失败");
    }


}
