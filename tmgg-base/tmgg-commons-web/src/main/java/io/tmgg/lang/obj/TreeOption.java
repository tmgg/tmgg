package io.tmgg.lang.obj;

import io.tmgg.lang.Tree;
import io.tmgg.lang.TreeManager;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * antd 树状选择对象
 */
@Data
public class TreeOption implements Tree<TreeOption> {
    String title;
    String key;

    String parentKey;

    List<TreeOption> children = new ArrayList<>();


    boolean selectable = true;
    boolean checkable = true;
    boolean disabled = false;

    Boolean isLeaf;


    Object data;


    public Object getValue() {
        return key;
    }

    /**
     * 同 key，为了方便
     *
     *
     */
    @Override
    public String getId() {
        return (String) key;
    }


    /**
     * 同 parentKey
     *
     *
     */
    @Override
    public String getPid() {
        return (String) parentKey;
    }

    public TreeOption() {
    }

    public TreeOption(String title, String key, String parentKey) {
        this.title = title;
        this.key = key;
        this.parentKey = parentKey;
    }

    public TreeOption(String title, String key, String parentKey, boolean disabled) {
        this.title = title;
        this.key = key;
        this.parentKey = parentKey;
        this.disabled = disabled;
    }


    public static List<TreeOption> convertTree(List<TreeOption> list) {
        TreeManager<TreeOption> tm = TreeManager.of(list);
        return tm.getTree();
    }


    public static <T> List<TreeOption> convertTree(Iterable<T> list, Function<T, String> valueFn, Function<T, String> parentValueFn, Function<T, String> labelFn) {
        return convertTree(list, valueFn, parentValueFn, labelFn, null);
    }


    public static <T> List<TreeOption> convertTree(Iterable<T> list, Function<T, String> valueFn, Function<T, String> parentValueFn, Function<T, String> labelFn, Function<T, Boolean> selectableFn) {
        List<TreeOption> treeList = getTreeOptions(list, valueFn, parentValueFn, labelFn, selectableFn);

        return convertTree(treeList);
    }




    private static <T> List<TreeOption> getTreeOptions(Iterable<T> list, Function<T, String> valueFn, Function<T, String> parentValueFn, Function<T, String> labelFn, Function<T, Boolean> selectableFn) {
        List<TreeOption> treeList = new ArrayList<>();
        for (T t : list) {
            TreeOption option = new TreeOption(labelFn.apply(t), valueFn.apply(t), parentValueFn.apply(t));
            if (selectableFn != null) {
                Boolean selectable = selectableFn.apply(t);
                if (selectable != null) {
                    option.setCheckable(selectable);
                    option.setSelectable(selectable);
                }
            }
            treeList.add(option);
        }
        return treeList;
    }

}
