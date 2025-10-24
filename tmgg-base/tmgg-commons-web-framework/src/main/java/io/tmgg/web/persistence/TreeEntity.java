package io.tmgg.web.persistence;


import io.tmgg.lang.TreeNode;

public interface TreeEntity<T> extends PersistEntity, TreeNode<T> {

    void setPid(String pid);

    String getPid();

    void setSeq(Integer i);

    Integer getSeq();
}
