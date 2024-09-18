package io.tmgg.web.base;

/**
 * 枚举的中文信息， 继承自本接口的枚举，会自动加入到数据字典中
 */
public interface DictEnum {

     String getMessage();

     default StatusColor getColor(){
          return StatusColor.DEFAULT;
     }
}
