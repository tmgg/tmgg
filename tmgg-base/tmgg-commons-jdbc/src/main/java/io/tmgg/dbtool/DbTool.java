package io.tmgg.dbtool;


import io.tmgg.dbtool.dbutil.MyBeanProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.*;
import org.apache.commons.dbutils.handlers.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;


@Slf4j
public class DbTool {

    private QueryRunner runner;

    private DataSource ds = null;


    private Config cfg = null;

    public DbTool() {
        log.info("执行构造函数：{}", getClass().getName());
    }

    public DbTool(DataSource dataSource) {
        this();
        this.ds = dataSource;
        this.runner = new QueryRunner(dataSource);
        this.cfg = new Config();
    }

    public DbTool(DataSource dataSource, Config config) {
        this(dataSource);
        this.cfg = config;
    }


    public QueryRunner getRunner() {
        if (runner == null && ds != null) {
            runner = new QueryRunner(ds);
        }
        return runner;
    }

    public void register(Converter converter) {
        Converters.getInstance().register(converter);
    }


    public int dropTable(String tableName) {
        return this.execute("DROP TABLE IF EXISTS " + tableName);
    }

    public int createTable(Class<?> cls) {
        return this.createTable(cls, _Util.underline(cls.getSimpleName()));
    }

    public int createTable(Class<?> cls, String tableName) {
        String sql = TableGenerator.generateCreateTableSql(cls, tableName);
        return this.execute(sql);
    }


    public <T> T findOne(Class<T> cls, String sql, Object... params) {
        params = checkParam(params);


        ResultSetHandler<T> rsh = this.getBeanHandler(cls);

        return this.query(sql, rsh, params);
    }

    /***
     *  returns a Map of Beans
     *
     * 查询列表，返回一个map，启用主键作为 map的key， bean 作为map的值
     *
     *  例如 select id, name, age from user;
     *
     *  返回的就是 {
     *      1: {name: "张三”, age: 19}.
     *      2: {name: "李四“, age: 24}
     *  }
     *
     *  @param <K>
     *       the type of keys maintained by the returned map
     *  @param <V>
     *       the type of the bean
     *
     * @see BeanMapHandler
     *
     */
    public <K, V> Map<K, V> findBeanMap(Class<V> cls, String sql, Object... params) {
        params = checkParam(params);

        BeanMapHandler<K, V> beanMapHandler = new BeanMapHandler<>(cls, getRowProcessor());

        return this.query(sql, beanMapHandler, params);
    }

    /***
     *  returns a Map of Maps
     *
     *  例如 select id, name, age from user;
     *
     *  返回的就是 {
     *      1: {name: "张三”, age: 19}.
     *      2: {name: "李四“, age: 24}
     *  }
     *
     *
     * @see BeanMapHandler
     *
     */
    public <K> Map<K, Map<String, Object>> findKeyed(String sql, Object... params) {
        params = checkParam(params);

        KeyedHandler<K> handler = new KeyedHandler<>();

        Map<K, Map<String, Object>> result = this.query(sql, handler, params);

        if (cfg.getNamingStrategy() == Config.NAMING_STRATEGY_IMPROVED) {
            for (K k : result.keySet()) {
                Map<String, Object> value = result.get(k);
                Map<String, Object> cameled = _Util.camel(value);
                result.put(k, cameled);
            }
        }
        return result;
    }


    /**
     * 返回字典
     * 先查询列表，将前两个字段组装成map
     * <p>
     * 如 select id, name from user
     * <p>
     * 结果
     * { 1: "张三”,  2: "李四" }
     *
     * @param sql
     * @param params
     * @param <K>
     * @param <V>
     * @return
     */
    public Map<String, Object> findDict(String sql, Object... params) {
        List<Map<String, Object>> list = this.findAll(sql, params);

        LinkedHashMap<String, Object> dict = new LinkedHashMap<>();

        for (Map<String, Object> row : list) {
            if (row.size() < 2) {
                throw new IllegalStateException("result size error");
            }
            Iterator<String> ite = row.keySet().iterator();

            String k1 = ite.next();
            String k2 = ite.next();

            Object v1 = row.get(k1);
            Object v2 = row.get(k2);
            dict.put((String) v1, v2);
        }
        return dict;
    }


    public <T> List<T> findAll(Class<T> cls, String sql, Object... params) {
        params = checkParam(params);
        ResultSetHandler<List<T>> rsh = new BeanListHandler<>(cls, getRowProcessor());

        List<T> list = this.query(sql, rsh, params);

        return list == null ? Collections.emptyList() : list;
    }

    public List<Map<String, Object>> findAll(String sql, Object... params) {
        params = checkParam(params);

        ResultSetHandler<List<Map<String, Object>>> rsh = new MapListHandler();
        List<Map<String, Object>> list = this.query(sql, rsh, params);
        if (list == null) {
            return Collections.emptyList();
        }
        if (cfg.getNamingStrategy() == Config.NAMING_STRATEGY_IMPROVED) {
            list = _Util.camel(list);
        }
        return list;
    }


    public Map<String, Object> findOne(String sql, Object... params) {
        params = checkParam(params);
        MapHandler rsh = new MapHandler();
        Map<String, Object> map = this.query(sql, rsh, params);

        if (map == null) {
            return null;
        }

        if (cfg.getNamingStrategy() == Config.NAMING_STRATEGY_IMPROVED) {
            map = _Util.camel(map);
        }
        return map;
    }


    public <T> Page<T> findAll(Class<T> cls, Pageable pageable, String sql, Object... params) {
        params = checkParam(params);

        String countSql = SqlPageableTool.getCountSql(sql);
        Long total = null;
        total = findLong(countSql, params);
        if (total == null)
            return new PageImpl<>(Collections.emptyList(), pageable, 0);

        String pageSql = SqlPageableTool.getPageSql(cfg.getDbType(), sql, pageable.getPageNumber(), pageable.getPageSize());

        List<T> list = findAll(cls, pageSql, params);

        return new PageImpl<>(list, pageable, total);
    }


    public Page<Map<String, Object>> findAll(Pageable pageable, String sql, Object... params) {
        params = checkParam(params);
        Long total = findLong(SqlPageableTool.getCountSql(sql), params);

        String pageSql = SqlPageableTool.getPageSql(cfg.getDbType(), sql, pageable.getPageNumber(), pageable.getPageSize());

        List<Map<String, Object>> list = this.findAll(pageSql, params);

        Page<Map<String, Object>> page = new PageImpl<>(list, pageable, total);
        if (cfg.getNamingStrategy() == Config.NAMING_STRATEGY_IMPROVED) {
            List<Map<String, Object>> content = _Util.camel(page.getContent());

            page = new PageImpl<>(content, pageable, page.getTotalElements());
        }
        return page;
    }


    /**
     * 返回 单个值
     * 如 select count(*) from user
     */
    public Object findScalar(String sql, Object... params) {
        params = checkParam(params);

        return this.query(sql, new ScalarHandler<>(), params);
    }


    public Long findLong(String sql, Object... params) {
        params = checkParam(params);

        Object result = this.query(sql, new ScalarHandler<>(), params);
        if (result instanceof Long) {
            return (Long) result;
        }

        if (result instanceof Integer) {
            return ((Integer) result).longValue();
        }

        if (result != null) {
            return Long.parseLong(result.toString());
        }

        return null;

    }


    public Integer findInteger(String sql, Object... params) {
        Long l = this.findLong(sql, params);
        if (l != null) {
            return l.intValue();
        }
        return null;
    }


    public <T> Page<T> findColumnPage(Pageable pageable, String sql, Object... params) {
        params = checkParam(params);
        Long total = findLong(SqlPageableTool.getCountSql(sql), params);

        String pageSql = SqlPageableTool.getPageSql(cfg.getDbType(), sql, pageable.getPageNumber(), pageable.getPageSize());

        List<T> list = this.findColumnList(pageSql, params);

        return new PageImpl<>(list, pageable, total);
    }


    /**
     * 查询某一列
     *
     * @param sql
     * @param params
     * @param <T>
     * @return
     */
    public <T> List<T> findColumnList(String sql, Object... params) {
        params = checkParam(params);
        List<T> list = null;
        list = this.query(sql, new ColumnListHandler<>(), params);
        return list == null ? Collections.emptyList() : list;
    }


    public int update(String sql, Object... params) {
        params = checkParam(params);

        try {
            return getRunner().update(sql, params);
        } catch (SQLException e) {
            throw new JdbcException(e);
        }
    }


    public int[] batch(String sql, Object[][] params) {
        if (params == null) {
            params = new Object[0][0];
        }
        try {
            return getRunner().batch(sql, params);
        } catch (SQLException e) {
            throw new JdbcException(e);
        }
    }

    public int[] batch(String sql) {
        try {
            return getRunner().batch(sql, new Object[0][0]);
        } catch (SQLException e) {
            log.info("批量执行sql异常：{}", e.getMessage());
            throw new JdbcException(e);
        }
    }


    public int execute(String sql, Object... params) {
        try {
            return getRunner().execute(sql, params);
        } catch (SQLException e) {
            throw new JdbcException(e);
        }
    }

    public int executeQuietly(String sql, Object... params) {
        try {
            return getRunner().execute(sql, params);
        } catch (SQLException e) {
            log.error("可忽略的sql异常 {}", e.getMessage());
            log.error(sql);
        }
        return 0;
    }

    public String[] getKeys(String sql) {
        sql = sql.replace("?", "''");
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getRunner().getDataSource().getConnection();

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            ResultSetMetaData metaData = rs.getMetaData();

            String[] ks = new String[metaData.getColumnCount()];
            for (int i = 1; i <= ks.length; i++) {
                ks[i - 1] = metaData.getColumnLabel(i);
            }
            return ks;
        } catch (Exception ex) {
            throw new JdbcException(ex);
        } finally {
            DbUtils.closeQuietly(conn, ps, rs);
        }

    }

    @SuppressWarnings("rawtypes")
    private Object[] checkParam(Object... params) {
        if (params != null && params.length == 1) {
            Object object = params[0];
            if (object instanceof Collection) {
                Collection col = (Collection) object;
                if (col.isEmpty()) {
                    return null;
                }
            }
        }
        return params;
    }


    /**
     * insert map data to table
     */
    public int insert(String tableName, Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(tableName).append(" (");


        Set<String> keySet = map.keySet();
        for (String key : keySet) {
            sb.append(key).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");

        sb.append(" values (");

        for (String ignored : keySet) {
            sb.append("?,");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");

        return this.update(sb.toString(), map.values().toArray());
    }

    public int updateById(String table, Map<String, Object> data) {
        StringBuilder sb = new StringBuilder();
        sb.append("update ").append(table).append(" set ");

        Set<String> keys = data.keySet();

        List<Object> params = new ArrayList<>();
        for (String key : keys) {
            if (key.equals("id")) {
                continue;
            }
            sb.append(_Util.underline(key)).append("=?,");
            params.add(data.get(key));
        }
        sb.deleteCharAt(sb.length() - 1);

        sb.append(" where id=?");
        params.add(data.get("id"));

        String sql = sb.toString();
        return this.update(sql, params.toArray());
    }


    public <T> T query(final String sql, final ResultSetHandler<T> rsh, final Object... params) {
        try {
            return getRunner().query(sql, rsh, params);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new JdbcException(e);
        }

    }

    // ------------------------------------元数据部分------------------------------

    public Set<String> getTableNames() throws SQLException {
        try (Connection conn = this.getRunner().getDataSource().getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();

            Set<String> tables;
            try (ResultSet resultSet = metaData.getTables(null, null, null, new String[]{"TABLE"})) {

                tables = new HashSet<>();
                while (resultSet.next()) {
                    String tableName = resultSet.getString("TABLE_NAME");
                    tables.add(tableName);
                }
            }

            return tables;
        }

    }


    public Set<String> getTableColumns(String tableName) throws SQLException {
        try (Connection conn = this.getRunner().getDataSource().getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();


            Set<String> set;
            try (ResultSet rs = metaData.getColumns(null, null, tableName, null)) {

                set = new HashSet<>();
                while (rs.next()) {
                    String columnName = rs.getString("COLUMN_NAME");
                    String columnType = rs.getString("TYPE_NAME");
                    System.out.println("Column Name: " + columnName + ", Type: " + columnType);
                    set.add(columnName);
                }
            }
            return set;
        }
    }

    public String getTableColumnType(String tableName, String columnName) throws SQLException {
        try (Connection conn = this.getRunner().getDataSource().getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();


            Set<String> set;
            try (ResultSet rs = metaData.getColumns(null, null, tableName, null)) {

                set = new HashSet<>();
                while (rs.next()) {
                    String n = rs.getString("COLUMN_NAME");

                    if (n.equals(columnName)) {
                        String columnType = rs.getString("TYPE_NAME");
                        return columnType;
                    }
                }
            }
        }
        return null;
    }


    /***
     *
     * @param sql
     * @return
     */
    private boolean hasOrderBy(String sql) {
        return sql.toLowerCase().contains("order by");
    }

    private <T> ResultSetHandler<T> getBeanHandler(Class<T> cls) {
        RowProcessor rowProcessor = getRowProcessor();

        return new BeanHandler<>(cls, rowProcessor);
    }

    private RowProcessor getRowProcessor() {
        RowProcessor rowProcessor = new BasicRowProcessor(new MyBeanProcessor());
        return rowProcessor;

    }
}
