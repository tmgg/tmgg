package io.tmgg.data.domain;

import com.fasterxml.jackson.annotation.*;
import io.tmgg.lang.ann.Remark;
import io.tmgg.web.persistence.DBConstants;
import io.tmgg.web.persistence.id.CustomGenerateIdProperties;
import io.tmgg.web.persistence.id.CustomId;
import io.tmgg.web.perm.SecurityUtils;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@MappedSuperclass
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
@EqualsAndHashCode(of = "id")
public abstract class BaseEntity implements PersistEntity, Serializable {

    public static final String FIELD_ID = "id";
    public static final String FIELD_CREATE_TIME = "createTime";
    public static final String FIELD_CREATE_USER = "createUser";
    public static final String FIELD_UPDATE_TIME = "updateTime";
    public static final String FIELD_UPDATE_USER = "updateUser";


    public static final String[] BASE_ENTITY_FIELDS = new String[]{FIELD_ID, FIELD_CREATE_TIME, FIELD_CREATE_USER, FIELD_UPDATE_TIME, FIELD_UPDATE_USER};


    public BaseEntity() {
    }

    public BaseEntity(String id) {
        this.id = id;
    }


    @Id
    @CustomId(style = CustomId.Style.UUID)
    @Column(length = DBConstants.LEN_ID)
    private String id;



    @Column(updatable = false)
    @Remark("创建时间")
    private Date createTime;


    @Remark("创建人ID")
    @Column(length = DBConstants.LEN_ID, updatable = false)
    private String createUser;


    private Date updateTime;



    @Remark("更新人ID")
    @Column(length = DBConstants.LEN_ID)
    private String updateUser;






    /**
     *  动态字段，处理实体中不包含的字段
     *  例如状态字段 status, 转成json希望动态增加字段 statusLabel
     */
    @Setter(AccessLevel.NONE) // lombok不生成setter
    @Transient
    @JsonAnySetter
    private Map<String, Object> extData = new HashMap<>();


    @JsonAnyGetter
    public Map<String,Object> getExtData(){
        return extData;
    }

    /**
     * 加入额外字段
     * @param key
     * @param value
     */
    public void putExtData(String key, Object value){
            extData.put(key,value);
    }


    @PrePersist
    public void prePersist() {
        this.prePersistOrUpdate();
        Date now = new Date();

        if (this.createTime == null) { // 有些异步保存的数据，时间上有些许差异。 可提前设置createTime，防止差异发生
            this.createTime = now;
        }
        this.updateTime = now;

        if (SecurityUtils.getSubject()!= null) {
            if (this.createUser == null) {
                String userId = SecurityUtils.getSubject().getId();
                this.updateUser = this.createUser = userId;
            }
        }

    }

    /**
     * 修改前调用
     * 注意：如果修改前后属性无变化，jpa不会入库，也就不会调用本方法
     */
    @PreUpdate
    public void preUpdate() {
        this.prePersistOrUpdate();
        this.updateTime = new Date();
        if (SecurityUtils.getSubject()!= null) {
            if (this.updateUser == null) {
                this.updateUser = SecurityUtils.getSubject().getId();
            }
        }
    }


    public void prePersistOrUpdate() {
    }




    @JsonIgnore
    @Transient
    public boolean isNew() {
        return null == getId();
    }


    /**
     * 新增时，自定义ID。
     */
    @JsonIgnore
    @Transient
    private String _tempId;


    // 动态生成的自定义Id
    @Transient
    @JsonIgnore
    @Override
    public String customGenerateId(CustomGenerateIdProperties properties) {
        return null;
    }

}
