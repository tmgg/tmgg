
package io.tmgg.sys.controller;

import io.tmgg.lang.TreeDefinition;
import lombok.Data;

import java.util.List;

/**
 * 系统字典树
 *
 *
 */
@Data
public class SysDictTreeNode implements TreeDefinition<SysDictTreeNode> {

    private String id;

    private String pid;

    private Object code;
    private String name;
    private String color;

    private List<SysDictTreeNode> children;

    private Boolean isLeaf;
}
