package io.tmgg.lang.dao;

import io.tmgg.lang.dao.specification.ExpressionTool;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.dao.specification.Selector;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.*;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;

/**
 * 基础dao
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

    public Page<T> findAll( Specification<T> spec, Pageable pageable) {
        return rep.findAll(spec,pageable);
    }

    public List<T> findAll(Specification<T> spec, Sort sort) {
        return rep.findAll(spec,sort);
    }

    public boolean exists(Specification<T> spec) {
        return rep.exists(spec);
    }

    @Transactional
    public long delete(Specification<T> spec) {
        return rep.delete(spec);
    }

    public <R> R findBy(Specification<T> spec, Function<FluentQuery.FetchableFluentQuery<T>, R> queryFunction) {
        return  rep.findBy(spec,queryFunction);
    }

    public T findOne(Example<T> example) {
      return rep.findOne(example).orElse(null);
    }

    public  long count(Example<T> example) {
        return rep.count(example);

    }

    public boolean exists(Example<T> example) {
        return rep.exists(example);
    }

    public  List<T> findAll(Example<T> example) {
        return rep.findAll(example);
    }

    public  List<T> findAll(Example<T> example, Sort sort) {
        return rep.findAll(example,sort);
    }

    public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {
        return rep.findAll(example,pageable);
    }

    public < R> R findBy(Example<T> example, Function<FluentQuery.FetchableFluentQuery<T>, R> queryFunction) {
      return rep.findBy(example,queryFunction);
    }

    public long count() {
        return rep.count();

    }

    public long count(Specification<T> spec) {
        return rep.count(spec);
    }

    @Transactional
    public T save(T entity) {
        return rep.save(entity);
    }

    @Transactional
    public T saveAndFlush(T entity) {
        return rep.saveAndFlush(entity);
    }

    @Transactional
    public  List<T> saveAll(Iterable<T> entities) {
        return rep.saveAll(entities);
    }

    @Transactional
    public  List<T> saveAllAndFlush(Iterable<T> entities) {
        return rep.saveAllAndFlush(entities);
    }

    @Transactional
    public void flush() {
        entityManager.flush();
    }


    public List<T> findByExampleLike(T t) {
        JpaQuery<T> query = new JpaQuery<>();
        query.likeExample(t);
        return this.findAll(query);
    }


    public boolean isFieldUnique(String id, String fieldName, Object value) {
        JpaQuery<T> c = new JpaQuery<>();
        if (id != null) {
            c.ne("id", id);
        }

        c.eq(fieldName, value);
        return count(c) == 0;
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


    public Page<T> findAll(T t, Pageable pageable) {
        JpaQuery<T> c = new JpaQuery<>();
        c.likeExample(t);
        return findAll(c, pageable);
    }


    public List<T> findAllLike(T example) {
        JpaQuery<T> c = new JpaQuery<>();
        c.likeExample(example);
        return this.findAll(c);
    }


    public List<T> findAllLike(T example, Sort sort) {
        JpaQuery<T> c = new JpaQuery<>();
        c.likeExample(example);
        return this.findAll(c, sort);
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
        query.select(path);

        Predicate expression = c.toPredicate(root, query, builder);
        query.where(expression);


        return (List<R>) entityManager.createQuery(query).getResultList();
    }





    /**
     * 查询一个单值
     * 如select sum(age) from user
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

        query.select(selection).where(spec.toPredicate(root,query,builder));

        return  entityManager.createQuery(query).getSingleResult();
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

        List<Object> resultList =  entityManager.createQuery(query).getResultList();

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
