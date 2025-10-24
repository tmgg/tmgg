package io.tmgg.modules.system.dto;

import io.tmgg.lang.Tree;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class MenuDto implements Tree<MenuDto> {

  String id;


  String pid;

  String rootid; // 如果使用

  String label;


  String path;

  String icon;


  String key;

  Boolean refreshOnTabClick;


  // 权限 ,临时字段
  @JsonIgnore
  String perm;


  List<MenuDto> children = new LinkedList<>();







  public MenuDto(String id, String pid, String label, String path, String icon) {
    this.id = id;
    this.pid = pid;
    this.label = label;
    this.path = path;
    this.icon = icon;

    if (path == null) {
      this.path = "";
    }


    this.key = id;
  }




  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    MenuDto route = (MenuDto) o;

    return new EqualsBuilder().append(id, route.id).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(id).toHashCode();
  }


  @Override
  public String toString() {
    return "Route{" +
           "id='" + id + '\'' +
           ", name='" + label + '\'' +
           ", perm='" + perm + '\'' +
           '}';
  }
}
