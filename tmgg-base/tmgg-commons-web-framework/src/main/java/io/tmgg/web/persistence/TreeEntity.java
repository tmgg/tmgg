package io.tmgg.web.persistence;


import io.tmgg.lang.Tree;

public interface TreeEntity<T> extends PersistEntity, Tree<T> {

    void setPid(String pid);

    String getPid();

    void setSeq(Integer i);

    Integer getSeq();
}
