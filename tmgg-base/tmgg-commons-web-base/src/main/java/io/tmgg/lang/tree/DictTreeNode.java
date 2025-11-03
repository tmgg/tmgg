package io.tmgg.lang.tree;

import cn.hutool.core.lang.Dict;

import java.util.List;

public class DictTreeNode extends Dict implements TreeNode<DictTreeNode> {
    @Override
    public String getId() {
        return this.getStr("id");
    }

    @Override
    public String getPid() {
        return this.getStr("pid");
    }

    @Override
    public List<DictTreeNode> getChildren() {
        return (List<DictTreeNode>) this.get("children");
    }

    @Override
    public void setChildren(List<DictTreeNode> list) {
        this.set("children", list);
    }

    @Override
    public void setIsLeaf(Boolean b) {
        this.set("isLeaf",b);
    }


    @Override
    public void setId(String id) {
        this.put("id",id);
    }

    @Override
    public void setPid(String pid) {
        this.put("pid",pid);
    }
}
