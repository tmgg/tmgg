package com.xxx.car.dao;

import com.xxx.car.entity.Car;
import io.tmgg.dbtool.DbTool;
import io.tmgg.persistence.BaseDao;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class CarDao extends BaseDao<Car> {

    @Resource
    private DbTool db; // 可直接调用sql


}
