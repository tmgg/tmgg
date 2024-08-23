package io.tmgg.lang.dao;

import io.tmgg.dbtool.DbTool;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.dao.specification.Selector;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.provider.PersistenceProvider;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import static org.springframework.data.jpa.repository.query.QueryUtils.*;

/**
 * 基础dao，可支持sql
 *
 */
@Slf4j
public abstract class BaseDao<T extends Persistable<String>> {

    @PersistenceContext
    protected EntityManager em;

    protected Class<T> domainClass = null;
    protected JpaEntityInformation<T, ?> entityInformation;
    private PersistenceProvider provider;

    /**
     * 方便直接调用sql， 当然也可自行注入
     */
    @Autowired(required = false)
    protected DbTool jdbc;


    @PostConstruct
    void init() {
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
            this.domainClass = (Class<T>) genericTypes[0];
        }

        this.entityInformation = JpaEntityInformationSupport.getEntityInformation(domainClass, em);
        this.provider = PersistenceProvider.fromEntityManager(em);
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
        return em.find(domainClass, id);
    }


    public T findOne(T t) {
        String id = t.getId();
        if (id == null) {
            return null;
        }
        return this.findOne(id);
    }


    public T findOneByField(String fieldName, Object value) {
        JpaQuery<T> query = new JpaQuery<>();
        query.eq(fieldName, value);
        return this.findOne(query);
    }


    public T findOneByField(String fieldName1, Object value1, String fieldName2, Object value2) {
        JpaQuery<T> c = new JpaQuery<>();
        c.eq(fieldName1, value1);
        c.eq(fieldName2, value2);
        return this.findOne(c);
    }


    public Map<String, T> findKeyed(Iterable<String> ids) {
        List<T> list = this.findAllById(ids);
        Map<String, T> map = new HashMap<>();
        for (T t : list) {
            map.put(t.getId(), t);
        }
        return map;
    }


    public List<T> findAllByField(String fieldName, Object value) {
        JpaQuery<T> c = new JpaQuery<>();
        c.eq(fieldName, value);
        return this.findAll(c);
    }


    public List<T> findAllByField(String fieldName1, Object value1, String fieldName2, Object value2) {
        JpaQuery<T> c = new JpaQuery<>();
        c.eq(fieldName1, value1);
        c.eq(fieldName2, value2);
        return this.findAll(c);
    }


    public List<T> findAllById(Iterable<String> ids) {
        if (ids == null || !ids.iterator().hasNext()) {
            return new ArrayList<>();
        }

        ByIdsSpecification<T> specification = new ByIdsSpecification<>(entityInformation);
        TypedQuery<T> query = getQuery(specification, Sort.unsorted());


        return query.setParameter(specification.parameter, ids).getResultList();
    }


    public List<T> findAllById(String[] ids) {
        if (ids == null || ids.length == 0) {
            return new ArrayList<>();
        }
        List<String> ls = new ArrayList<>();

        ls.addAll(Arrays.asList(ids));

        return this.findAllById(ls);

    }

    public List<T> findAll(Iterable<T> list) {
        if (list == null) {
            return Collections.emptyList();
        }

        List<String> ids = new ArrayList<>();
        for (T t : list) {
            ids.add(t.getId());
        }

        return this.findAllById(ids);
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


    public List<T> findChildren(String id, String parentProperty) {
        JpaQuery<T> c = new JpaQuery<>();
        c.eq(parentProperty, id);
        List<T> list = this.findAll(c);

        List<T> result = new ArrayList<>();
        if (list != null && !list.isEmpty()) {
            result.addAll(list);
            for (T child : list) {
                List<T> subChildren = findChildren(child.getId(), parentProperty);
                result.addAll(subChildren);
            }
        }
        return result;
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
        return null;
    }

    public <R> List<R> findField(String fieldName, Specification<T> c) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery q = cb.createQuery();
        Root root = q.from(domainClass);
        Path path = root.get(fieldName);
        q.select(path);

        Predicate expression = c.toPredicate(root, q, cb);
        q.where(expression);


        return (List<R>) em.createQuery(q).getResultList();
    }


    @Transactional
    public void deleteAllById(String[] ids) {
        List<T> list = this.findAllById(ids);
        this.deleteAll(list);
    }


    @Transactional
    public void deleteAllById(Collection<String> ids) {
        List<T> list = this.findAllById(ids);
        Assert.state(list.size() == ids.size(), "删除数量与数据库中数量不一致，请稍后再试");
        this.deleteAll(list);
    }


    public List<T> findAll() {
        return getQuery(null, Sort.unsorted()).getResultList();
    }

    public Map<String, T> findDict() {
        List<T> all = this.findAll();

        Map<String, T> map = new HashMap<>();
        for (T t : all) {
            String id = t.getId();
            map.put(id, t);
        }
        return map;
    }


    public List<T> findAll(Sort sort) {
        return getQuery(null, sort).getResultList();
    }


    public Page<T> findAll(Pageable pageable) {
        if (isUnpaged(pageable)) {
            return new PageImpl<>(findAll());
        }

        return findAll(new JpaQuery<>(), pageable);
    }

    public Page<T> findAll(Map<String, Object> map, Pageable pageable) {

        JpaQuery<T> query = new JpaQuery<>();

        Set<Map.Entry<String, Object>> entries = map.entrySet();
        for (Map.Entry<String, Object> e : entries) {
            String k = e.getKey();
            Object v = e.getValue();
            if(v != null){
                query.like( k, v);
            }

        }

        return findAll(query, pageable);
    }

    public T findOne(Specification<T> spec) {
        TypedQuery<T> query = getQuery(spec, Sort.unsorted());
        List<T> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        if (resultList.size() > 1) {
            throw new IllegalStateException("查询结果大于1");
        }
        return resultList.get(0);
    }


    public List<T> findAll(Specification<T> spec) {
        TypedQuery<T> query = getQuery(spec, Sort.unsorted());
        return query.getResultList();
    }

    /**
     * 聚合函数
     *
     * @param spec : 查询条件， 可简单通过JpaQuery
     * @param selector 选择器
     *
     * @return 结果
     */
    public Object[] findAggregate(Specification<T> spec, Selector selector) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<Object> criteriaQuery = builder.createQuery(Object.class);

        Root<T> root = applySpecificationToCriteria(spec, criteriaQuery);


        criteriaQuery.multiselect(selector.select(builder, root));

        // Remove all Orders the Specifications might have applied
        criteriaQuery.orderBy(Collections.emptyList());

        TypedQuery<Object> typedQuery = em.createQuery(criteriaQuery);

        return (Object[]) typedQuery.getSingleResult();
    }


    public Page<T> findAll(Specification<T> spec, Pageable pageable) {
        TypedQuery<T> query = getQuery(spec, pageable);
        return isUnpaged(pageable) ? new PageImpl<>(query.getResultList())
                : readPage(query, pageable, spec);
    }


    public Page<T> findAll(Specification<T> spec, Pageable pageable, Sort sort) {
        TypedQuery<T> query = getQuery(spec, getDomainClass(), sort);
        return isUnpaged(pageable) ? new PageImpl<>(query.getResultList())
                : readPage(query, pageable, spec);
    }


    public List<T> findAll(Specification<T> spec, Sort sort) {
        TypedQuery<T> query = getQuery(spec, sort);
        return query.getResultList();
    }


    public void deleteOne(Specification<T> spec) {
        T one = this.findOne(spec);
        this.delete(one);
    }

    public long count(@Nullable Specification<T> spec) {
        return executeCountQuery(getCountQuery(spec));
    }


    public long count() {
        return this.count(null);
    }


    @Transactional
    public void deleteById(String id) {
        if (id == null) {
            return;
        }
        T db = this.findOne(id);
        if (db == null) {
            return;
        }

        this.delete(db);
    }


    @Transactional
    public void delete(T entity) {
        if (entity == null) {
            return;
        }
        if (entityInformation.isNew(entity)) {
            return;
        }

        T existing = em.find(entityInformation.getJavaType(), entityInformation.getId(entity));
        // if the entity to be deleted doesn't exist, delete is a NOOP
        if (existing == null) {
            return;
        }
        boolean contains = em.contains(entity);
        em.remove(contains ? entity : em.merge(entity));
    }


    @Transactional
    public void deleteAll(Iterable<? extends T> entities) {
        if (entities == null || !entities.iterator().hasNext()) {
            return;
        }
        for (T entity : entities) {
            delete(entity);
        }
    }


    @Transactional
    public void deleteAll() {
        for (T element : findAll()) {
            delete(element);
        }
    }


    @Transactional
    public void deleteWithChildren(String id, String parentProperty) {
        List<T> list = findChildren(id, parentProperty);
        deleteById(id);
        if (list != null && !list.isEmpty()) {
            deleteAll(list);
        }
    }


    @Transactional
    public void deleteInBatch(Iterable<T> entities) {
        Assert.notNull(entities, "Entities must not be null!");

        if (!entities.iterator().hasNext()) {
            return;
        }

        applyAndBind(getQueryString(DELETE_ALL_QUERY_STRING, entityInformation.getEntityName()), entities, em)
                .executeUpdate();
    }


    @Transactional
    public void deleteAllInBatch() {
        em.createQuery(getDeleteAllQueryString()).executeUpdate();
    }


    @Transactional
    public T save(T entity) {
        if (entityInformation.isNew(entity)) {
            em.persist(entity);
            return entity;
        } else {
            return em.merge(entity);
        }
    }


    @Transactional
    public T saveAndFlush(T entity) {
        T result = save(entity);
        flush();

        return result;
    }


    @Transactional
    public List<T> saveAll(Iterable<T> entities) {
        List<T> result = new ArrayList<T>();

        for (T entity : entities) {
            result.add(save(entity));
        }

        return result;
    }


    public boolean existsById(String id) {
        if (id == null) {
            return false;
        }

        if (entityInformation.getIdAttribute() == null) {
            return findOne(id) != null;
        }

        String placeholder = provider.getCountQueryPlaceholder();
        String entityName = entityInformation.getEntityName();
        Iterable<String> idAttributeNames = entityInformation.getIdAttributeNames();
        String existsQuery = QueryUtils.getExistsQueryString(entityName, placeholder, idAttributeNames);

        TypedQuery<Long> query = em.createQuery(existsQuery, Long.class);

        if (!entityInformation.hasCompositeId()) {
            query.setParameter(idAttributeNames.iterator().next(), id);
            return query.getSingleResult() == 1L;
        }

        for (String idAttributeName : idAttributeNames) {

            Object idAttributeValue = entityInformation.getCompositeIdAttributeValue(id, idAttributeName);

            boolean complexIdParameterValueDiscovered = idAttributeValue != null
                                                        && !query.getParameter(idAttributeName).getParameterType().isAssignableFrom(idAttributeValue.getClass());

            if (complexIdParameterValueDiscovered) {

                // fall-back to findById(id) which does the proper mapping for the parameter.
                return findOne(id) != null;
            }

            query.setParameter(idAttributeName, idAttributeValue);
        }

        return query.getSingleResult() == 1L;
    }


    @Transactional
    public void flush() {
        em.flush();
    }


    protected TypedQuery<T> getQuery(@Nullable Specification<T> spec, Sort sort) {
        return getQuery(spec, getDomainClass(), sort);
    }

    protected Class<T> getDomainClass() {
        return entityInformation.getJavaType();
    }

    protected TypedQuery<T> getQuery(@Nullable Specification<T> spec, Class<T> domainClass, Sort sort) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(domainClass);

        Root<T> root = applySpecificationToCriteria(spec, query);
        query.select(root);

        if (sort.isSorted()) {
            query.orderBy(toOrders(sort, root, builder));
        }

        return em.createQuery(query);
    }

    protected TypedQuery<T> getQuery(@Nullable Specification<T> spec, Pageable pageable) {
        Sort sort = pageable.isPaged() ? pageable.getSort() : Sort.unsorted();
        return getQuery(spec, getDomainClass(), sort);
    }

    protected Page<T> readPage(TypedQuery<T> query, Pageable pageable,
                               @Nullable Specification<T> spec) {

        if (pageable.isPaged()) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }

        return PageableExecutionUtils.getPage(query.getResultList(), pageable,
                () -> executeCountQuery(getCountQuery(spec)));
    }

    private static long executeCountQuery(TypedQuery<Long> query) {

        Assert.notNull(query, "TypedQuery must not be null!");

        List<Long> totals = query.getResultList();
        long total = 0L;

        for (Long element : totals) {
            total += element == null ? 0 : element;
        }

        return total;
    }

    protected TypedQuery<Long> getCountQuery(@Nullable Specification<T> spec) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);


        Root<T> root = applySpecificationToCriteria(spec, query);

        if (query.isDistinct()) {
            query.select(builder.countDistinct(root));
        } else {
            query.select(builder.count(root));
        }

        // Remove all Orders the Specifications might have applied
        query.orderBy(Collections.emptyList());

        return em.createQuery(query);
    }

    private String getDeleteAllQueryString() {
        return getQueryString(DELETE_ALL_QUERY_STRING, entityInformation.getEntityName());
    }


    private static boolean isUnpaged(Pageable pageable) {
        return pageable.isUnpaged();
    }

    private <S> Root<T> applySpecificationToCriteria(@Nullable Specification<T> spec,
                                                     CriteriaQuery<S> query) {

        Assert.notNull(domainClass, "Domain class must not be null!");
        Assert.notNull(query, "CriteriaQuery must not be null!");

        Root<T> root = query.from(domainClass);

        if (spec == null) {
            return root;
        }

        CriteriaBuilder builder = em.getCriteriaBuilder();
        Predicate predicate = spec.toPredicate(root, query, builder);

        if (predicate != null) {
            query.where(predicate);
        }

        return root;
    }


    public void deleteAll(JpaQuery query) {
        List list = this.findAll(query);
        this.deleteAll(list);
    }

    public boolean exist(String id) {
        return this.existsById(id);
    }

    public boolean exist(JpaQuery<T> query) {
       return this.count(query) > 0;
    }


    private static final class ByIdsSpecification<T> implements Specification<T> {

        private static final long serialVersionUID = 1L;

        private final JpaEntityInformation<T, ?> entityInformation;

        @Nullable
        ParameterExpression<Iterable> parameter;

        ByIdsSpecification(JpaEntityInformation<T, ?> entityInformation) {
            this.entityInformation = entityInformation;
        }

        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

            Path<?> path = root.get(entityInformation.getIdAttribute());
            parameter = cb.parameter(Iterable.class);
            return path.in(parameter);
        }
    }
}
