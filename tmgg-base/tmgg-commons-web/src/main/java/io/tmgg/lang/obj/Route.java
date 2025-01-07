package io.tmgg.lang.obj;

import io.tmgg.lang.Tree;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public class Route implements Tree<Route> {

  String id;


  String pid;

  String label;


  String path;

  String icon;


  String key;


  // 权限 ,临时字段
  @JsonIgnore
  String perm;

  Boolean iframe;

  List<Route> children = new LinkedList<>();

  int badge = 0;




  public Route() {
  }


  public Route(String id, String pid, String label, String path, String icon) {
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

    Route route = (Route) o;

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
