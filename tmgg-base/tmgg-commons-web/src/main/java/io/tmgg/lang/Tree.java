package io.tmgg.lang;

import java.util.List;


public interface Tree<T> {
    String getId();

    String getPid();


    List<T> getChildren();

   void setChildren(List<T> list);


    default void setIsLeaf(Boolean b) {

    }
}
