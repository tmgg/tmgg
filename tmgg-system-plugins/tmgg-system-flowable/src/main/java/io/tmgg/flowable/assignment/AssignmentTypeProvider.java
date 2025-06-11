package io.tmgg.flowable.assignment;

import org.apache.commons.lang3.NotImplementedException;

import java.util.Collection;
import java.util.List;

/**
 * 分配类型
 */
public interface AssignmentTypeProvider {


    String getCode();

    String getLabel();

    int getOrder();


    // 是否多选
    boolean isMultiple();


    String getLabelById(String id);


    /**
     * bpm xml 文件的属性
     *
     * @return
     */
    XmlAttribute getXmlAttribute();


    /**
     * 列表
     *
     * @return
     */
    Collection<Identity> findAll();


    /**
     * 获得用户所属分组 （如角色，分组等）， 用于运行时查询，
     * 例如 角色分 管理员， 如果你时管理员，就能看到待办
     */
    default List<String> findGroupsByUser(String userId) {
        return null;
    }


    default boolean isRelativeGroup() {
        return false;
    }

    /**
     * startUserId 可以用于获取相对分组候选人, 如部门领导， 上级公司经理, 对于非相对角色，可为空
     * 非相对分组可不实现
     *
     * @return
     */
    default Collection<String> findUsersByGroup(String startUserId, String groupId) {
        if (getXmlAttribute() == XmlAttribute.candidateGroups) {
            throw new NotImplementedException("当节点属于分组时，应实现该方法");
        }
        return null;
    }


    /**
     * 设置审批者的标准属性，
     */
    enum XmlAttribute {
        assignee,
        candidateGroups,
        candidateUsers,
    }
}
