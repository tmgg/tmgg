package io.tmgg.sys.watermask.service;

import io.tmgg.lang.dao.BaseDao;
import io.tmgg.lang.dao.BaseService;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.sys.watermask.entity.Watermark;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WatermarkService extends BaseService<Watermark> {



    public List<String> findValid(){
        JpaQuery<Watermark> q = new JpaQuery<>();
        q.eq("enable", true);
        List<Watermark> list = this.findAll(q);

        return list.stream().map(Watermark::getPath).collect(Collectors.toList());
    }



}
