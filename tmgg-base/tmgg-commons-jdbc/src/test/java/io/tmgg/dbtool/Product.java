package io.tmgg.dbtool;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class Product {

    String id;
    String name;

    Double price;

    Date createTime;

    ProjectType productType;
    ProjectType productTypeStr;

    List<String> tags;


}
