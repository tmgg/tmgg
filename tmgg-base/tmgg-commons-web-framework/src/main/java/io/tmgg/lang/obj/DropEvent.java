package io.tmgg.lang.obj;

import lombok.Data;

@Data
public class DropEvent {

    String dropKey;
    String dragKey;
    int dropPosition;
    boolean dropToGap; // 两个节点的关系，true表示平级

}
