package io.tmgg.web.persistence;

/**
 * 自动填充字段
 *
 *
 * 页面显示通常会包含更多的字段，例如一个实体存了orgId，但希望显示orgLabel
 * 通常会增加一个字段 orgLabel, 并使用@Transient注解标注，然后手动设置。
 * 可以在orgId字段上使用自动注解 @AutoAppend
 * ```java
 *     {@literal @}AutoAppendField(AutoAppendOrgLabelStrategy.class)
 *     private String orgId;
 *
 *     {@literal @}Transient
 *     private String orgLabel;
 * ```
 * 开发者可扩展，秩序实现AutoAppendStrategy接口，
 * 目前内置的有
 * - AutoAppendDictLabelStrategy 字典名称
 * - AutoAppendEnumLabelStrategy 枚举注释
 * - AutoAppendFileViewUrlStrategy 文件预览地址
 * - AutoAppendOrgLabelStrategy 机构名称
 * - AutoAppendUserLabelStrategy 用户名称
 *
 * @gendoc
 */
public interface AutoAppendStrategy {


    Object getAppendValue(Object bean, Object sourceValue, String param);

}
