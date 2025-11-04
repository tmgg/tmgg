package io.tmgg.lang;


import io.tmgg.lang.tree.TreeNode;

import java.util.*;

/**
 * 将列表转换为树,请使用TreeManager
 */
@Deprecated
public class TreeTool {


    public static <T extends TreeNode> List<T> buildTree(Collection<T> list) {
        // 构建 map，方便快查询
        Map<Object, T> map = new LinkedHashMap();
        for (T t : list) {
            map.put(t.getId(), t);
        }

        List<T> root = new ArrayList<>();
        for (T t : list) {
            Object pid = t.getPid();

            boolean isRoot = pid == null || !map.containsKey(pid);
            if (isRoot) {
                root.add(t);
                continue;
            }

            TreeNode parent = map.get(pid);
            if(parent.getChildren() == null){
                parent.setChildren(new ArrayList());
            }
            parent.getChildren().add(t);
        }

        checkAndSetLeaf(map.values());
        return root;
    }


    private static <T extends TreeNode> void checkAndSetLeaf(Collection<T> values) {
        for (T v : values) {
            List<T> children = v.getChildren();
            boolean isLeaf = children == null || children.isEmpty();
            if (isLeaf) {
                v.setChildren(null);
            }
            v.setIsLeaf(isLeaf);
        }
    }


}
