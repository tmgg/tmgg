
package io.tmgg.lang.obj;

import io.tmgg.lang.Tree;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

/**
 * 菜单树节点
 *
 */
@Getter
@Setter
public class TreeNode implements Tree<TreeNode> {

    /**
     * 主键
     */
    private String id;

    private String pid;



    /**
     * 名称
     */
    private String title;

    /**
     * 值
     */
    private String value;

    /**
     * 排序，越小优先级越高
     */
    private Integer weight;

    /**
     * 子节点
     */
    private List<TreeNode> children;


    private Boolean isLeaf;
  public String getParentId() {
    return pid;
  }

  public String getKey() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    TreeNode that = (TreeNode) o;

    return new EqualsBuilder().append(id, that.id).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(id).toHashCode();
  }
}
