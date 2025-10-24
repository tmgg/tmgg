package io.tmgg.lang.tree;

import java.util.List;


public interface TreeNode<T> {
    String getId();

    void setId(String id);

    String getPid();

    void setPid(String id);

    List<T> getChildren();

   void setChildren(List<T> list);


    default void setIsLeaf(Boolean b) {

    }
}
