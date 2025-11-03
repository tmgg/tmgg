package io.tmgg.web.persistence.converter;

import io.tmgg.data.domain.BaseEntity;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class XUser extends BaseEntity {

    String userName;

    UserType userType;

}
