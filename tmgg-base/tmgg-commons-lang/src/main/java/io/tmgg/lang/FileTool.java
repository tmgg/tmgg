package io.tmgg.lang;

import java.io.File;

public class FileTool {

    public static final File findParentByName(File file, String parentName) {
        while (file != null) {
            if (file.getName().equals(parentName)) {
                return file;
            }
            file = file.getParentFile();
        }
        return null;

    }
}
