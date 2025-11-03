package io.tmgg.lang.tree.drop;

import cn.hutool.core.collection.ListUtil;
import io.tmgg.lang.tree.TreeNode;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

/**
 * 计算拖拽后，排序后的列表数据
 * @param <T>
 */
public class TreeDropTool  {
    public  interface FindByIdAction<T>{
         T findById(String id);

    }

    public  interface FindByPidAction<T>{

        List<T> findByPid(String pid);
    }


    public static <T extends TreeNode<T>> List<T> onDrop(TreeDropEvent e, FindByIdAction<T> findByIdAction, FindByPidAction<T> findByPidAction) {
        String dropKey = e.getDropKey();
        String dragKey = e.getDragKey();
        int dropPosition = e.getDropPosition();

        T dragNode = findByIdAction.findById(dragKey);
        T dropNode = findByIdAction.findById(dropKey);

        String pid = e.isDropToGap() ? dropNode.getPid() : dropNode.getId();
        dragNode.setPid(pid); // 更新pid

        // 获得兄弟节点
        List<T> list = findByPidAction.findByPid(pid);
        if (list.size() < 2) {
            return list;
        }

        // 交换位置
        swap(list, dropPosition, dragNode);

        return list;
    }

    private static <T extends TreeNode<T>> void swap(List<T> list, int dropPosition, T dragNode) {
        int swapPos = dropPosition;
        if (dropPosition == -1) { // 最前
            swapPos = 0;
        } else if (dropPosition == list.size()) { // 最后
            swapPos = list.size() - 1;
        }

        ListUtil.swapTo(list, dragNode, swapPos);
    }

}
