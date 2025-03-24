package com.xxx.car.service;

import com.xxx.car.dao.CarDao;
import com.xxx.car.entity.Car;
import io.tmgg.lang.dao.BaseService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class CarService extends BaseService<Car> {

    @Resource
    private CarDao dao;




}
