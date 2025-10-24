package io.tmgg.modules.system.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class GrantMenuToRoleRequest {
    String id;
    List<String> menuIds;
}
