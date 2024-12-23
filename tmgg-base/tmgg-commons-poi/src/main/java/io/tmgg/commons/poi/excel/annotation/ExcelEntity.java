/**
 * Copyright 2013-2015 JueYue (qrb.jueyue@gmail.com)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.tmgg.commons.poi.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记是不是导出excel 标记为实体类
 * @author JueYue
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelEntity {

    /**
     * 定义excel导出ID 来限定导出字段,处理一个类对应多个不同名称的情况
     */
    public String id() default "";

    /**
     * 导出时，对应数据库的字段 主要是用户区分每个字段， 不能有annocation重名的 导出时的列名
     * 导出排序跟定义了annotation的字段的顺序有关 可以使用a_id,b_id来确实是否使用
     */
    public String name() default "";

    /**
     * 如果等于true,name必须有值, Excel的表头会变成两行,同时改Excel内部数据不参与总排序,排序用下面这个来代替,内部再排序
     * 排序取当中最小值排序
     * @return
     */
    public boolean show() default false;

}
