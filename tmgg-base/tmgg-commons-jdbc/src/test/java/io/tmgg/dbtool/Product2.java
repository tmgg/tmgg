package io.tmgg.dbtool;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class Product2 {

    String id;
    String name;

    BigDecimal price;

    LocalDateTime createTime;

    ProjectType productType;
    ProjectType productTypeStr;

    List<Integer> tags;


}
