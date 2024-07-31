
package io.tmgg.sys.org.entity;

import io.tmgg.lang.TreeDefinition;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.sys.org.enums.OrgType;
import io.tmgg.web.enums.CommonStatus;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 系统组织机构表
 */
@Getter
@Setter
@Entity
@FieldNameConstants
public class SysOrg extends BaseEntity implements TreeDefinition<SysOrg> {

    public SysOrg() {
    }

    public SysOrg(String id) {
        super(id);
    }

    /**
     * 父id, 如果是根节点，则为空
     */
    private String pid;


    /**
     * 名称
     */
    @NotNull
    private String name;

    private String shortName;


    /**
     * 排序
     */
    private Integer seq;


    /**
     * 状态（字典 0正常 1停用 2删除）
     */
    @NotNull
    private CommonStatus status;


    @NotNull
    @Enumerated(EnumType.STRING)
    private OrgType type;


    // 预留字段1
    private String reservedField1;

    // 预留字段2
    private String reservedField2;

    // 预留字段3
    private String reservedField3;


    @Transient
    List<SysOrg> children;


    /**
     * 拼音, 如字节跳动： zjtd
     */
    @Column(length = 500)
    private String pinyin;


    @Override
    public void beforeSaveOrUpdate() {
        super.beforeSaveOrUpdate();
        pinyin = PinyinUtil.getFirstLetter(name, ""); // 缩写
        pinyin += ":" + PinyinUtil.getPinyin(name, ""); // 全屏

        System.out.println(name + "的拼音为：" + pinyin);
    }


    @Transient
    public String getBestName() {
        return StrUtil.emptyToDefault(shortName, name);
    }


    @Transient
    public boolean isDept() {
        if (type == null) {
            return false;
        }
        return type.isDept();
    }


    @Override
    public String toString() {
        return name;
    }
}
