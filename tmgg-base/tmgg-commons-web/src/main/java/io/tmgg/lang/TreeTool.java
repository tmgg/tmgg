package io.tmgg.lang;


import java.util.*;

/**
 * 将列表转换为树
 */
public class TreeTool {


    public static <T extends Tree> List<T> buildTree(Collection<T> list) {
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

            Tree parent = map.get(pid);
            if(parent.getChildren() == null){
                parent.setChildren(new ArrayList());
            }
            parent.getChildren().add(t);
        }

        checkAndSetLeaf(map.values());
        return root;
    }

    public <T extends Tree> List<T> treeToList(Collection<T> tree) {
        List<T> result = new ArrayList<>();

        treeToList(tree, result);

        return result;
    }


    public static <T extends Tree> void findAllChildren(T node, Collection<T> result) {
        List<T> children = node.getChildren();

        if (children == null || children.isEmpty()) {
            return;
        }
        for (T t : children) {
            result.add(t);

            findAllChildren(t, result);
        }

    }

    private <T extends Tree> void treeToList(Collection<T> tree, List<T> result) {
        for (T t : tree) {
            result.add(t);
            treeToList(t.getChildren(), result);
        }
    }

    private static <T extends Tree> void checkAndSetLeaf(Collection<T> values) {
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
