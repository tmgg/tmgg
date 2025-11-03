package io.tmgg.lang.tree.drop;

import lombok.Data;

@Data
public class TreeDropEvent {

    String dropKey;
    String dragKey;
    int dropPosition;
    boolean dropToGap; // 两个节点的关系，true表示平级

}
