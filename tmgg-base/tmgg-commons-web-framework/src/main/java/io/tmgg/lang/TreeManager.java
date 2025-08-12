package io.tmgg.lang;

import cn.hutool.core.lang.Dict;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 树形管理， 内部包含很多数据，及方法
 */
public class TreeManager<T> {
    private static final int ROOT_LEVEL = 1;
    private List<T> list;
    private Function<T, String> idFn;
    private Function<T, String> pidFn;


    private Function<T, List<T>> getChildrenFn;
    private BiConsumer<T, List<T>> setChildrenFn;

    private List<T> tree;

    private Map<String, T> map;


    public TreeManager(List<T> list, Function<T, String> idFn, Function<T, String> pidFn, Function<T, List<T>> getChildrenFn, BiConsumer<T, List<T>> setChildrenFn) {
        this.list = list;
        this.idFn = idFn;
        this.pidFn = pidFn;
        this.getChildrenFn = getChildrenFn;
        this.setChildrenFn = setChildrenFn;

        buildMap();
        buildTree();
        cleanLeafChildren();
    }


    /**
     * map,约定字段，id，pid， children
     *
     * @param dataList 数据列表
     * @return 管理器
     */
    public static TreeManager<Map<String, Object>> ofMap(List<Map<String, Object>> dataList) {
        return new TreeManager<>(dataList, m -> (String) m.get("id"), m -> (String) m.get("pid"), m -> (List<Map<String, Object>>) m.get("children"), (m, ch) -> m.put("children", ch));
    }

    public static TreeManager<Dict> of(List<Dict> dataList, String idKey, String pidKey) {
        return new TreeManager<>(dataList, m -> (String) m.get(idKey), m -> (String) m.get(pidKey), m -> (List<Dict>) m.get("children"), (m, ch) -> m.put("children", ch));
    }


    public static <X extends Tree<X>> TreeManager<X> of(List<X> dataList) {
        TreeManager<X> tm = new TreeManager<>(dataList, Tree::getId, Tree::getPid, Tree::getChildren, Tree::setChildren);

        // 设置是否叶子
        for (X x : dataList) {
            x.setIsLeaf(tm.isLeaf(x));
        }

        return tm;
    }

    public static <X> void traverseTree(Collection<X> treeList, Function<X, List<X>> getChildrenFn, TraverseAction<X> traverseAction) {
        for (X item : treeList) {
            traverseAction.performAction(item);
            List<X> children = getChildrenFn.apply(item);
            if (children != null && children.size() > 0) {
                traverseTree(children, getChildrenFn, traverseAction);
            }
        }
    }

    public Map<String, T> getMap() {
        return map;
    }

    public List<T> getTree() {
        return tree;
    }

    public interface TraverseAction<T> {
        void performAction(T item);
    }


    public void traverseTree(List<T> treeList, TraverseAction<T> traverseAction) {
        for (T item : treeList) {
            traverseAction.performAction(item);
            List<T> children = getChildrenFn.apply(item);
            if (children != null && children.size() > 0) {
                traverseTree(children, traverseAction);
            }
        }
    }

    /**
     * 遍历树，从最深子节点到，父节点，
     *
     * @param traverseAction 回调
     */
    public void traverseTreeFromLeaf(TraverseAction<T> traverseAction) {
        Map<String, Integer> levelMap = this.buildLevelMap();
        int maxLevel = levelMap.values().stream().mapToInt(t -> t).max().getAsInt();

        for (int i = maxLevel; i >= ROOT_LEVEL; i--) {

            for (Map.Entry<String, Integer> e : levelMap.entrySet()) {
                String id = e.getKey();
                Integer level = e.getValue();
                if (level == i) {
                    traverseAction.performAction(getMap().get(id));
                }
            }
        }
    }

    public List<T> getSortedList() {
        List<T> list = new ArrayList<>();
        List<T> treeList = this.getTree();

        this.traverseTree(treeList, list::add);

        return list;
    }

    public T getParentById(String id) {
        T t = map.get(id);
        if (t == null) {
            return null;
        }
        String pid = pidFn.apply(t);
        return map.get(pid);
    }

    private void buildMap() {
        map = new HashMap<>();
        for (T t : list) {
            String id = idFn.apply(t);
            map.put(id, t);
        }
    }

    private void buildTree() {
        tree = new ArrayList<>(list.size() / 2);

        for (T t : list) {
            String pid = pidFn.apply(t);
            T parent = map.get(pid);
            if (parent != null) {
                List<T> children = initChildren(parent);
                children.add(t);
            } else {
                // root node
                tree.add(t);
            }
        }
    }

    private List<T> initChildren(T node) {
        List<T> children = getChildrenFn.apply(node);
        if (children != null) {
            return children;
        }

        children = new ArrayList<>();
        setChildrenFn.accept(node, children);
        return children;
    }

    private void cleanLeafChildren() {
        for (T t : getMap().values()) {
            if (isLeaf(t)) {
                setChildrenFn.accept(t, null);
            }
        }
    }


    public List<T> getAllChildren(String id) {
        T node = map.get(id);
        if (node == null) {
            return Collections.emptyList();
        }

        List<T> result = new ArrayList<>();

        addChildToResult(node, result);

        return result;
    }

    public T getParent(T t) {
        String pid = pidFn.apply(t);
        return map.get(pid);
    }

    public T getParent(T t, Function<T, Boolean> util) {
        T parent = this.getParent(t);
        if (parent == null) {
            return null;
        }
        Boolean result = util.apply(parent);
        if (result) {
            return parent;
        } else {
            return getParent(parent, util);
        }
    }

    public boolean isLeaf(String id) {
        T t = map.get(id);
        return isLeaf(t);
    }

    public boolean isLeaf(T t) {
        if (t == null) {
            return false;
        }
        List<T> children = getChildrenFn.apply(t);
        return children == null || children.isEmpty();
    }

    public int getLeafCount(T t) {
        if (t == null) {
            return 0;
        }

        AtomicInteger count = new AtomicInteger(0);
        traverseTree(Collections.singletonList(t), item -> {
            boolean leaf = isLeaf(item);
            if (leaf) {
                count.incrementAndGet();
            }
        });

        return count.get();
    }


    public List<T> getLeafList() {
        List<T> result = new ArrayList<>();
        for (T t : map.values()) {
            if (isLeaf(t)) {
                result.add(t);
            }
        }
        return result;
    }

    public List<String> getLeafIdList() {
        List<String> result = new ArrayList<>();
        for (T t : map.values()) {
            if (isLeaf(t)) {
                result.add(idFn.apply(t));
            }
        }
        return result;
    }


    public List<String> getParentIdListById(String id) {
        List<String> ids = new ArrayList<>();

        T t = map.get(id);
        if (t == null) {
            return Collections.emptyList();
        }
        T parent = getParent(t);

        while (parent != null) {
            String parentId = idFn.apply(parent);
            ids.add(0, parentId);

            T temp = getParent(parent);
            parent = temp;
        }
        return ids;
    }


    /**
     * 通过层级查询
     *
     * @param level 开始于 TreeManager.ROOT_LEVEL
     * @return 结果id列表
     */
    public List<String> getIdsByLevel(int level) {
        Map<String, Integer> lm = buildLevelMap();

        List<String> result = new ArrayList<>();

        for (Map.Entry<String, Integer> e : lm.entrySet()) {
            if (e.getValue() == level) {
                result.add(e.getKey());
            }
        }

        return result;
    }

    public int getLevelById(String id) {
        Map<String, Integer> lm = buildLevelMap();
        return lm.get(id);
    }


    public Map<String, Integer> buildLevelMap() {
        Map<String, Integer> result = new HashMap<>();

        for (T t : tree) {
            String id = idFn.apply(t);
            result.put(id, ROOT_LEVEL);
            setChildLevel(t, result);
        }

        return result;
    }


    private void setChildLevel(T t, Map<String, Integer> levelMap) {
        String id = idFn.apply(t);
        List<T> children = getChildrenFn.apply(t);
        if (children != null && !children.isEmpty()) {
            for (T child : children) {
                String cid = idFn.apply(child);
                levelMap.put(cid, levelMap.get(id) + 1);
                setChildLevel(child, levelMap);
            }
        }
    }


    private void addChildToResult(T t, List<T> result) {
        List<T> children = getChildrenFn.apply(t);
        if (children != null && !children.isEmpty()) {
            for (T child : children) {
                result.add(child);
                addChildToResult(child, result);
            }
        }
    }


}
