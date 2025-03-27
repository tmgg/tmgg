package io.tmgg.lang.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.GenericGenerator;


import java.io.Serializable;

@Getter
@Setter
@MappedSuperclass
@FieldNameConstants
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
@Slf4j
public abstract class BaseIdEntity implements PersistEntity, Serializable {

    @Id
    @GeneratedValue(generator = "custom-uuid")
    @GenericGenerator(name = "custom-uuid", strategy = IdGenerator.CLASS_NAME)
    @Column(length = DBConstants.LEN_ID, updatable = false)
    protected String id;




    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @PostLoad
    public void afterLoad(){
        BeanPropertyFillUtil.fillBeanProperties(this);
    }




    @JsonIgnore
    @Override
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
        if (!BaseIdEntity.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        BaseIdEntity other = (BaseIdEntity) obj;
        return getId() != null && getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        int hashCode = 17;

        hashCode += null == getId() ? 0 : getId().hashCode() * 31;

        return hashCode;
    }

}
