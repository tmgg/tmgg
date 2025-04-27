package io.tmgg.lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CollectionTool {


    public static <T extends Comparable<T>> Collection<T> findForAdd(Collection<T> oldList, Collection<T> list) {
        List<T> result = new ArrayList<>();
        for (T t : list) {
            if (!oldList.contains(t)) {
                result.add(t);
            }
        }

        return result;
    }



    public static <T extends Comparable<T>> Collection<T> findForUpdate(Collection<T> oldList, Collection<T> list) {
        List<T> result = new ArrayList<>();
        for (T t : list) {
            if (oldList.contains(t)) {
                result.add(t);
            }
        }

        return result;
    }


    public static <T extends Comparable<T>> Collection<T> findForDelete(Collection<T> oldList, Collection<T> list) {
        List<T> result = new ArrayList<>();
        for (T t : oldList) {
            if (!list.contains(t)) {
                result.add(t);
            }
        }

        return result;
    }

    public static <T> void fill(Collection<T> list, T item, int size){
        for (int i = 0; i < size; i++) {
            list.add(item);
        }
    }

    public static <T> void fillNull(Collection<T> list, int size){
       fill(list, null,size);
    }
}
