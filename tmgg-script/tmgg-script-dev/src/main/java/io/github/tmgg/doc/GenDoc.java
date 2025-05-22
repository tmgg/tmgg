package io.github.tmgg.doc;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.nodeTypes.NodeWithJavadoc;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;
import io.github.tmgg.DevFileUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class GenDoc {

    public static final File GEN_DOC = new File("docs/docs/gendoc.md");

    private static Map<String, File> FILE_MAP = new HashMap<>();

    public static void main(String[] args) throws Exception {
        Collection<File> javaFiles = DevFileUtil.findJavaFiles(".");
        for (File javaFile : javaFiles) {
            FILE_MAP.put(FileUtil.mainName(javaFile.getName()), javaFile);
        }


        // 清空文档
        FileUtil.writeUtf8String("", GEN_DOC);

        for (File javaFile : javaFiles) {
            List<Info> list = parseFile(javaFile);
            for (Info s : list) {
                if (StrUtil.isEmpty(s.content)) {
                    continue;
                }
                String str = convertString(s);
                System.out.println(s);
                FileUtils.writeStringToFile(GEN_DOC, str, "utf-8", true);
            }
        }

    }

    private static String convertString(Info info) {
        String content = info.content;
        if (StrUtil.isEmpty(content)) {
            return null;
        }
        content = StringUtils.replace(content, "{@literal @}", "@");
        content = StringUtils.replace(content, "@gendoc", "");


        String[] arr = content.split("\n");
        StringBuilder sb = new StringBuilder();
        sb.append("##  ");
        for (String s : arr) {
            if (s.contains("@see")) { // demo  将内容直接放进来
                String file = s.replace("@see", "").trim();
                String fileName = StringUtils.substringAfterLast(file, ".");
                File file1 = FILE_MAP.get(fileName);

                if (file1 != null) {
                    sb.append("示例代码\n");
                    String demoContent = FileUtil.readUtf8String(file1);
                    sb.append("```java\n").append(demoContent).append("\n```");
                } else {
                    sb.append(s);
                }
                continue;
            } else {
                sb.append(s);
            }

            sb.append("\n");
        }
        sb.append("\n\n");

        return sb.toString();
    }

    private static List<Info> parseFile(File path) throws FileNotFoundException {
        List<Info> list = new ArrayList<>();
        // 解析Java文件
        JavaParser parser = new JavaParser();
        CompilationUnit cu = parser.parse(path).getResult().orElse(null);
        if (cu == null) {
            return list;
        }

        List<Node> typeDeclarations = cu.findAll(Node.class);


        for (Node node : typeDeclarations) {
            if (node instanceof NodeWithJavadoc<?> d) {
                Javadoc javadoc = d.getJavadoc().orElse(null);
                if (javadoc != null) {
                    List<JavadocBlockTag> blockTags = javadoc.getBlockTags();
                    Info info = new Info();

                    for (JavadocBlockTag blockTag : blockTags) {
                        if (blockTag.getTagName().equals("gendoc")) {
                            info.content = javadoc.toText().trim();
                            list.add(info);
                        }
                    }
                }
            }

        }


        return list;
    }


    public static class Info {
        String content;

    }
}
