package io.tmgg.web.persistence;

import cn.hutool.core.collection.ListUtil;
import io.tmgg.lang.obj.DropEvent;
import io.tmgg.lang.tree.TreeNode;

import java.util.List;

public class TreeDropTool<T extends TreeNode<T>>  {


    public  interface FindAction<T>{
         T findById(String id);

         List<T> findByPid(String pid);
    }



    public  List<T> onDrop(DropEvent e, FindAction<T> findAction) {
        String dropKey = e.getDropKey();
        String dragKey = e.getDragKey();
        int dropPosition = e.getDropPosition();

        T dragNode = findAction.findById(dragKey);
        T dropNode = findAction.findById(dropKey);

        String pid = e.isDropToGap() ? dropNode.getPid() : dropNode.getId();
        dragNode.setPid(pid); // 更新pid

        // 获得兄弟节点
        List<T> list = findAction.findByPid(pid);
        if (list.size() < 2) {
            return list;
        }

        // 交换位置
        swap(list, dropPosition, dragNode);

        return list;
    }

    private void swap(List<T> list, int dropPosition, T dragNode) {
        int swapPos = dropPosition;
        if (dropPosition == -1) { // 最前
            swapPos = 0;
        } else if (dropPosition == list.size()) { // 最后
            swapPos = list.size() - 1;
        }

        ListUtil.swapTo(list, dragNode, swapPos);
    }

}
