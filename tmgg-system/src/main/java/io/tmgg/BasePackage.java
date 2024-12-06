package io.tmgg;


import io.tmgg.lang.SpringTool;

import java.util.*;

public class BasePackage {

    private static final String BASE_PACKAGE= BasePackage.class.getPackageName();

    public static Collection<String> getBasePackages(){
        Class custom = SpringTool.getBean(SysProp.class).getBasePackageClass();
        String customPackageName = custom.getPackageName();
        if(BASE_PACKAGE.equals(customPackageName)) {
            return Set.of(BASE_PACKAGE);
        }
        return Set.of(BASE_PACKAGE, customPackageName);
    }
}
