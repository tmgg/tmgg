package io.tmgg.lang.ann;

import java.lang.reflect.Field;

public class MsgTool {

    public static String getMsg(Field field) {
        if (field == null) {
            return null;
        }
        Msg annotation = field.getAnnotation(Msg.class);
        if (annotation != null) {
            return annotation.value();
        }
        return null;
    }

    public static String getMsg(Class<?> t) {
        if (t == null) {
            return null;
        }
        Msg annotation = t.getAnnotation(Msg.class);
        if (annotation != null) {
            return annotation.value();
        }
        return null;
    }

    public static String getMsg(Enum<?> t) {
        if (t == null) {
            return null;
        }
        try {
            Field f = t.getClass().getDeclaredField(t.name());
            Msg ann = f.getAnnotation(Msg.class);
            if (ann == null) {
                throw new RuntimeException(t.getClass().getSimpleName() + "没有设置注解@Remark");
            }
            String remark = ann.value();
            return remark;
        } catch (NoSuchFieldException | SecurityException e) {
            // do nothing
            e.printStackTrace();
        }
        return null;
    }
}
