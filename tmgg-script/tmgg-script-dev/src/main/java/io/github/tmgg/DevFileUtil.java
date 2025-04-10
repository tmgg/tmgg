package io.github.tmgg;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

public class DevFileUtil {

    public static List<File> find(String baseDir, String filename, List<String> dirs) {
        File dir = new File(baseDir);

        IOFileFilter fileFilter = new IOFileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().equals(filename);
            }

            @Override
            public boolean accept(File file, String name) {
                return false;
            }
        };
        IOFileFilter dirFilter = new IOFileFilter() {
            @Override
            public boolean accept(File file) {

                String name = file.getName();
                if(dirs.contains(name)){
                    return true;
                }

                String pName = file.getParentFile().getName();
                if(dirs.contains(pName)){
                    return true;
                }

                return false;
            }

            @Override
            public boolean accept(File file, String s) {
                return false;
            }
        };
        Collection<File> files = FileUtils.listFiles(dir, fileFilter, dirFilter);

        return new LinkedList<>(files);
    }

    public static Collection<File> findByPrefix(String baseDir, String filename, String prefix) {
        File dir = new File(baseDir);

        IOFileFilter fileFilter = new IOFileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().equals(filename);
            }

            @Override
            public boolean accept(File file, String name) {
                return false;
            }
        };
        IOFileFilter dirFilter = new IOFileFilter() {
            @Override
            public boolean accept(File file) {

              return   file.getName().startsWith(prefix);
            }

            @Override
            public boolean accept(File file, String s) {
                return false;
            }
        };
        Collection<File> files = FileUtils.listFiles(dir, fileFilter, dirFilter);

        return files;
    }


}
