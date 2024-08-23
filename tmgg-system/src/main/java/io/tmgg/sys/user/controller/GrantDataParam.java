package io.tmgg.sys.user.controller;

import io.tmgg.sys.user.enums.DataPermType;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
public class GrantDataParam {

    @NotNull
    String id;

    @NotNull
    DataPermType dataPermType;

    List<String> grantOrgIdList;

}
