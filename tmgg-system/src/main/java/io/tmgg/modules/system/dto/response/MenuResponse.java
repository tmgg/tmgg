package io.tmgg.modules.system.dto.response;

import io.tmgg.lang.tree.TreeNode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class MenuResponse  implements TreeNode<MenuResponse> {

  String id;


  String pid;

  /**
   * 根节点id， 比如用户管理的根节点为最顶层菜单系统管理， 用于前端自动切换顶层app菜单
   */
  String rootid; // 如果使用

  String label;


  String path;

  String icon;


  String key;

  Boolean refreshOnTabClick;



  List<MenuResponse> children = new LinkedList<>();


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    MenuResponse route = (MenuResponse) o;

    return new EqualsBuilder().append(id, route.id).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(id).toHashCode();
  }



}
