package io.tmgg.modules.system.dto;

import io.tmgg.modules.system.entity.DataPermType;
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
