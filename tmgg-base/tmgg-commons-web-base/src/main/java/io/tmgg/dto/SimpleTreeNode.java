
package io.tmgg.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

@Getter
@Setter
public class SimpleTreeNode implements io.tmgg.lang.tree.TreeNode<SimpleTreeNode> {

    private String id;

    private String pid;



    private String title;



    private List<SimpleTreeNode> children;


    public SimpleTreeNode(String id, String pid, String title) {
        this.id = id;
        this.pid = pid;
        this.title = title;
    }
}
