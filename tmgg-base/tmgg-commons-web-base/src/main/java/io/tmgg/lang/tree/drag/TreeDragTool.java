package io.tmgg.lang.tree.drag;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import io.tmgg.lang.tree.TreeNode;
import org.apache.poi.ss.formula.functions.T;

import java.util.ArrayList;
import java.util.List;

public class TreeDragTool  {


    public  interface FindAction<T>{
         T findById(String id);

         List<T> findByPid(String pid);
    }



    public static  <T extends TreeNode<T>>  List<T> onDrop(DragDropEvent e, List<T> all) {
        String dropKey = e.getDropKey();
        String dragKey = e.getDragKey();
        int dropPosition = e.getDropPosition();

        T dragNode = findById(all,dragKey);
        T dropNode = findById(all,dropKey);

        String pid = e.isDropToGap() ? dropNode.getPid() : dropNode.getId();
        dragNode.setPid(pid); // 更新pid

        // 获得兄弟节点
        List<T> list = findByPid(all,pid);
        if (list.size() < 2) {
            return list;
        }

        // 交换位置
        int swapPos = dropPosition;
        if (dropPosition == -1) { // 最前
            swapPos = 0;
        } else if (dropPosition == list.size()) { // 最后
            swapPos = list.size() - 1;
        }
        ListUtil.swapTo(list, dragNode, swapPos);


        return list;
    }


    private static  <T extends TreeNode<T>> T findById(List<T> nodes, String id){
        for (T node : nodes) {
            if (node.getId().equals(id)) {
                return node;
            }
        }
        return null;
    }

    private static  <T extends TreeNode<T>> List<T> findByPid(List<T> nodes, String pid){
        List<T> children = new ArrayList<>();
        for (T node : nodes) {
            if (ObjectUtil.equals(pid, node.getPid())) {
              children.add(node);
            }
        }
        return children;
    }
}
