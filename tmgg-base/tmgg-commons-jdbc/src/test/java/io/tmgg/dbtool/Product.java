package io.tmgg.dbtool;

import lombok.Data;

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
