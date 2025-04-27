/**
 * Copyright 2013-2015 JueYue (qrb.jueyue@gmail.com)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.tmgg.commons.poi.word;

import cn.hutool.core.io.FileUtil;
import io.tmgg.commons.poi.word.entity.MyXWPFDocument;
import io.tmgg.commons.poi.word.parse.ParseWord07;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;

/**
 * Word使用模板导出工具类
 *

 *  变量例子： {{dept}}
 *
 * @author JueYue
 *  2013-11-16
 * @version 1.0
 */
public class WordExportUtil {



    public static XWPFDocument exportWord07(InputStream is, Map<String, Object> map) throws Exception {
        MyXWPFDocument doc = new MyXWPFDocument(is);
        new ParseWord07().parseWord(doc, map);
        return doc;
    }

    /**
     *
     * @param is
     * @param map
     * @param prefix 文件前缀，可为空
     * @return
     * @throws Exception
     */
    public static File exportTempFile(InputStream is, Map<String, Object> map, String prefix) throws Exception {
        MyXWPFDocument doc = new MyXWPFDocument(is);
        new ParseWord07().parseWord(doc, map);

        if(prefix == null){
            prefix = "word_";
        }

        File tempFile = FileUtil.createTempFile(prefix,".docx",false);

        FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
        doc.write(fileOutputStream);
        fileOutputStream.close();
        doc.close();

        return tempFile;
    }

}
