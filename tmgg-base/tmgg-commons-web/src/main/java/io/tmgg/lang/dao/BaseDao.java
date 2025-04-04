package io.tmgg.lang.dao;

import io.tmgg.lang.dao.id.EntityIdHolder;
import io.tmgg.lang.dao.specification.ExpressionTool;
import io.tmgg.lang.dao.specification.Selector;
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

    public T getReferenceById(String id) {
        return rep.getReferenceById(id);
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
        return rep.save(entity);
    }

    @Transactional
    public T saveAndFlush(T entity) {
        return rep.saveAndFlush(entity);
    }

    @Transactional
    public List<T> saveAll(Iterable<T> entities) {
        return rep.saveAll(entities);
    }

    @Transactional
    public List<T> saveAllAndFlush(Iterable<T> entities) {
        return rep.saveAllAndFlush(entities);
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

    /**
     * 主要解决设置了ID，但不知道数据库是否存在的情况，常见于需要自定义id的场景
     * 如果id存在先查询数据库，再决定保存或更新
     * 类似于mysql的replace
     *
     * @param entity
     * @return
     */
    @Transactional
    public T replace(T entity) {
        String id = entity.getId();
        if (id == null) {
            return this.insert(entity);
        }

        boolean isNew =  !existsById(id);
        if (isNew) {
            // hibernate 升级后的问题，如果制定了id生成器，就不能自定义id了
            EntityIdHolder.cache(entity,id);
            entity.setId(null);
            entityManager.persist(entity);
            return entity;
        }
        entityManager.merge(entity);
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
