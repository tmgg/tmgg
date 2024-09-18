package io.tmgg.lang.obj;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 选项，如下拉多选，单选等
 */
@Data
public class Option {
    String label;
    Object value;

    Object data;

    public Option() {
    }

    public Option(String label, Object value) {
        this.label = label;
        this.value = value;
    }

    public Option(String label, Object value, Object data) {
        this.label = label;
        this.value = value;
        this.data = data;
    }


    public static  List<Option> empty(){
        return new ArrayList<>();
    }

    public static <T> List<Option> convertList(Iterable<T> list, Function<T, Object> valueFn, Function<T, String> labelFn){
        List<Option> result = new ArrayList<>();
        for(T t : list){
            String label = labelFn.apply(t);
            Object value = valueFn.apply(t);
            result.add(new Option(label, value));
        }

        return result;
    }

    /**
     * 转换字符串列表未 OptionList
     * @param list
     *
     */
    public static  List<Option> convertList(Iterable<String> list){
        List<Option> result = new ArrayList<>();
        for(String t : list){
            result.add(new Option(t, t));
        }

        return result;
    }


    public static <T> List<Option> convertListWithSelf(Iterable<T> list, Function<T, Object> valueFn, Function<T, String> labelFn){
        List<Option> result = new ArrayList<>();
        for(T t : list){
            String label = labelFn.apply(t);
            Object value = valueFn.apply(t);
            result.add(new Option(label, value, t));
        }

        return result;
    }

}
