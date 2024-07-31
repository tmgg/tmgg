//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.tmgg.web.excel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Excel {
    String databaseFormat() default "yyyyMMddHHmmss";

    String exportFormat() default "";

    String importFormat() default "";

    String format() default "";

    String timezone() default "";

    String numFormat() default "";

    /** @deprecated */
    @Deprecated
    double height() default 10.0;

    int imageType() default 1;

    String suffix() default "";

    boolean isWrap() default true;

    int[] mergeRely() default {};

    boolean mergeVertical() default false;

    String name();

    String groupName() default "";

    boolean needMerge() default false;

    String orderNum() default "0";

    String[] replace() default {};

    String dict() default "";

    boolean addressList() default false;

    String savePath() default "/excel/upload/img";

    int type() default 1;

    double width() default 10.0;

    boolean isStatistics() default false;

    boolean isHyperlink() default false;

    String isImportField() default "false";

    int fixedIndex() default -1;

    boolean isColumnHidden() default false;

    String enumExportField() default "";

    String enumImportMethod() default "";

    String desensitizationRule() default "";
}
