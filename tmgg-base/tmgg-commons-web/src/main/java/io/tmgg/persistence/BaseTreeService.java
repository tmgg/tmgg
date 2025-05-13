package io.tmgg.persistence;

import cn.hutool.core.collection.ListUtil;
import io.tmgg.persistence.specification.JpaQuery;
import io.tmgg.lang.obj.DropEvent;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class BaseTreeService<T extends TreeEntity<T>> extends BaseService<T> {

    public List<T> findByPid(String pid) {
        JpaQuery<T> q = new JpaQuery<>();

        if(pid == null){
            q.isNull("pid");
        }else {
            q.eq("pid", pid);
        }

        return baseDao.findAll(q, Sort.by("seq"));
    }


    @Transactional
    public void onDrop(DropEvent e) {
        String dropKey = e.getDropKey();
        String dragKey = e.getDragKey();
        int dropPosition = e.getDropPosition();

        T dragNode = baseDao.findOne(dragKey);
        T dropNode = baseDao.findOne(dropKey);

        String pid = e.isDropToGap() ? dropNode.getPid() : dropNode.getId();
        dragNode.setPid(pid); // 更新pid

        List<T> list = this.findByPid(pid);
        if (list.size() < 2) {
            return;
        }

        // 交换位置
        swap(dropPosition, list, dragNode);

        // 更新序号
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setSeq(i);
        }
    }

    private void swap(int dropPosition, List<T> list, T dragNode) {
        int swapPos = dropPosition;
        if (dropPosition == -1) { // 最前
            swapPos = 0;
        } else if (dropPosition == list.size()) { // 最后
            swapPos = list.size() - 1;
        }

        ListUtil.swapTo(list, dragNode, swapPos);
    }

}
