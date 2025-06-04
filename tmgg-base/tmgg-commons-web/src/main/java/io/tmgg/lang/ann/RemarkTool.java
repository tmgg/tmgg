package io.tmgg.lang.ann;

import java.lang.reflect.Field;

public class RemarkTool {

    public static String getMsg(Field field) {
        if (field == null) {
            return null;
        }
        Remark annotation = field.getAnnotation(Remark.class);
        if (annotation != null) {
            return annotation.value();
        }
        return null;
    }

    public static String getMsg(Class<?> t) {
        if (t == null) {
            return null;
        }
        Remark annotation = t.getAnnotation(Remark.class);
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
            Remark ann = f.getAnnotation(Remark.class);
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
