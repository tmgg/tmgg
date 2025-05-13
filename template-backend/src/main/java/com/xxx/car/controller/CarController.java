package com.xxx.car.controller;


import com.xxx.car.entity.Car;
import com.xxx.car.service.CarService;
import io.tmgg.persistence.BaseCURDController;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("car")
public class CarController extends BaseCURDController<Car> {

    @Resource
    public CarService service;



}
