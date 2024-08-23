
package io.tmgg.sys.user.param;

import io.tmgg.web.enums.CommonStatus;
import io.tmgg.web.validation.group.*;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 系统用户参数
 *
 */

@Getter
@Setter
public class SysUserParam  {

    /**
     * 主键
     */
    @NotNull(message = "id不能为空，请检查id参数",
            groups = {Edit.class, Delete.class,  Start.class,
                    Stop.class,
                 ChangeStatus.class})
    private String id;

    /**
     * 账号
     */
    @NotBlank(message = "账号不能为空，请检查account参数", groups = {Add.class, Edit.class})
    private String account;

    /**
     * 密码
     */
    private String password;

    /**
     * 新密码
     */
    private String newPassword;


    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空，请检查name参数", groups = {Add.class, Edit.class})
    private String name;

    /**
     * 头像
     */
    private String avatar;


    /**
     * 手机
     */
    @NotNull(message = "手机号码不能为空，请检查phone参数", groups = {Add.class, Edit.class})
    @Size(min = 11, max = 11, message = "手机号码格式错误，请检查phone参数", groups = {Add.class, Edit.class})
    private String phone;



    /**
     * 授权角色
     */
    private List<String> grantRoleIdList;



    /**
     * 状态（字典 0正常 1停用 2删除）
     */
    @NotNull(message = "状态不能为空，请检查status参数", groups = ChangeStatus.class)
    private CommonStatus status;


    private String orgId;

    private String deptId;


    private List<String> roleIds;

    private String email;
}
