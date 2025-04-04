package io.tmgg.lang.dao;

import com.fasterxml.jackson.annotation.*;
import io.tmgg.lang.SpringTool;
import io.tmgg.lang.ann.Msg;
import io.tmgg.lang.dao.exports.UserLabelQuery;
import io.tmgg.lang.dao.id.CustomId;
import io.tmgg.web.perm.SecurityUtils;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@MappedSuperclass
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
@Slf4j
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
    @CustomId
    @Column(length = DBConstants.LEN_ID)
    private String id;


    @Column(updatable = false)
    @Msg("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Msg("创建人ID")
    @Column(length = DBConstants.LEN_ID, updatable = false)
    private String createUser;





    /**
     *
     * @return  创建人名称
     */
    @Transient
    public String getCreateUserLabel() {
        UserLabelQuery userLabelQuery = SpringTool.getBean(UserLabelQuery.class);
        if(userLabelQuery != null && createUser != null){
            return userLabelQuery.getNameById(createUser);
        }
        return null;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;


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
        this.updateTime = this.createTime;

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


    @PostLoad
    public void afterLoad() {
            BeanPropertyFillUtil.fillBeanProperties(this);
    }


    @JsonIgnore
    @Transient
    public boolean isNew() {
        String theId = getId();
        return null == theId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!BaseEntity.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        BaseEntity other = (BaseEntity) obj;
        return getId() != null && getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        int hashCode = 17;

        hashCode += null == getId() ? 0 : getId().hashCode() * 31;

        return hashCode;
    }



}
