package io.tmgg.sys.dto;

import io.tmgg.sys.user.enums.DataPermType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GrantPermDto {

    @NotNull
    String id;

    @NotNull
    DataPermType dataPermType;

    List<String> orgIds;

    List<String> roleIds;

}