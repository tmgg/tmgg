package io.tmgg.lang.tree;

import java.util.List;


public interface TreeNode<T> {
    String getId();

    default void setId(String id) {

    }

    String getPid();

    default void setPid(String id) {

    }

    List<T> getChildren();

   void setChildren(List<T> list);


    default void setIsLeaf(Boolean b) {

    }
}
