package io.github.tmgg.doc;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithJavadoc;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;
import io.github.tmgg.DevFileUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

public class GenDoc {

    public static final File GEN_DOC = new File("docs/docs/gendoc.md");

    public static void main(String[] args) throws Exception {
        Collection<File> javaFiles = DevFileUtil.findJavaFiles(".");

     //   javaFiles = new ArrayList<>();
    //    javaFiles.add(new File("D:\\ws\\tmgg8\\tmgg-base\\tmgg-commons-web\\src\\main\\java\\io\\tmgg\\web\\persistence\\specification\\JpaQuery.java"));

        // 清空文档
        FileUtil.writeUtf8String("", GEN_DOC);

        for (File javaFile : javaFiles) {
            List<String> list = parseFile(javaFile);
            for (String s : list) {
                if (StrUtil.isEmpty(s)) {
                    continue;
                }
                s = filter(s);
                System.out.println(s);
                FileUtils.writeStringToFile(GEN_DOC, s, "utf-8", true);
            }
        }

    }

    private static String filter(String s) {
        if (StrUtil.isEmpty(s)) {
            return null;
        }

        s = StringUtils.replace(s, "{@literal @}", "@");
        s = StringUtils.replace(s, "@gendoc", "");

        s = "##  " + s + "\n\n";
        return s;
    }

    private static List<String> parseFile(File path) throws FileNotFoundException {
        List<String> list = new ArrayList<>();
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
                    for (JavadocBlockTag blockTag : blockTags) {
                        if (blockTag.getTagName().equals("gendoc")) {
                            list.add(javadoc.toText());
                        }
                    }
                }
            }

        }


        return list;
    }

}
