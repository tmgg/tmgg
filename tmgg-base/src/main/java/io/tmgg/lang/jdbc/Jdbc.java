package io.tmgg.lang.jdbc;

import cn.moon.dbtool.DbTool;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.sql.DataSource;
import java.util.Map;

public class Jdbc extends DbTool {

    public Jdbc(DataSource dataSource) {
        super(dataSource);
    }

    public <T> Page<T> findAll(Class<T> cls, Pageable pageable, String sql,Object... params) {
        cn.moon.lang.web.Pageable p = new  cn.moon.lang.web.Pageable(false,pageable.getPageNumber(), pageable.getPageSize());
        cn.moon.lang.web.Page<T> page = super.findAll(cls, p, sql, params);

        return new PageImpl<>(page.getContent(), pageable, page.getTotalElements());
    }

    public Page<Map<String, Object>> findAll(Pageable pageable, String sql, Object... params) {
        cn.moon.lang.web.Pageable p = new  cn.moon.lang.web.Pageable(false,pageable.getPageNumber(), pageable.getPageSize());
        cn.moon.lang.web.Page<Map<String, Object>> page = super.findAll(p, sql, params);

        return new PageImpl<>(page.getContent(), pageable, page.getTotalElements());
    }
}
