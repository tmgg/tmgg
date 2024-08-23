
package io.tmgg.sys.role.param;

import io.minio.messages.Grant;
import io.tmgg.web.validation.group.Add;
import io.tmgg.web.validation.group.Delete;
import io.tmgg.web.validation.group.Edit;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.*;
import java.util.List;

/**
 * 系统角色参数
 *

 *
 */

@Getter
@Setter
public class SysRoleParam  {

    /**
     * 主键
     */
    @NotNull(message = "id不能为空，请检查id参数", groups = {Edit.class, Delete.class,  Grant.class})
    private String id;

    /**
     * 名称
     */
    @NotBlank(message = "名称不能为空，请检查name参数", groups = {Add.class, Edit.class})
    private String name;

    /**
     * 编码
     */
    @NotBlank(message = "编码不能为空，请检查code参数", groups = {Add.class, Edit.class})
    private String code;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 数据范围类型（字典 1全部数据 2本部门及以下数据 3本部门数据 4仅本人数据 5自定义数据）
     */
    @Null(message = "数据范围类型应该为空， 请移除dataScopeType参数", groups = {Add.class, Edit.class})
    @NotNull(message = "数据范围类型不能为空，请检查dataScopeType参数", groups = {Grant.class})
    @Min(value = 0, message = "数据范围类型格式错误，请检查dataScopeType参数", groups = {Grant.class})
    @Max(value = 5, message = "数据范围类型格式错误，请检查dataScopeType参数", groups = {Grant.class})
    private Integer dataScopeType;

    /**
     * 备注
     */
    private String remark;

    /**
     * 授权菜单
     */
    @NotNull(message = "授权菜单不能为空，请检查grantMenuIdList参数", groups = {Grant.class})
    private List<String> grantMenuIdList;

    /**
     * 授权数据
     */
    @NotNull(message = "授权数据不能为空，请检查grantOrgIdList参数", groups = {Grant.class})
    private List<String> grantOrgIdList;


    private List<String> dataScope;
}
