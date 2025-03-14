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
package io.tmgg.commons.poi.excel;

import io.tmgg.commons.poi.excel.entity.ImportParams;
import io.tmgg.commons.poi.excel.entity.result.ExcelImportResult;
import io.tmgg.commons.poi.excel.imports.ExcelImportService;
import io.tmgg.commons.poi.exception.excel.ExcelImportException;
import io.tmgg.commons.poi.handler.inter.IReadHandler;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Excel 导入工具
 *
 * @author JueYue
 *  2013-9-24
 * @version 1.0
 */
@SuppressWarnings({ "unchecked" })
public class ExcelImportUtil {

    private ExcelImportUtil() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelImportUtil.class);

    /**
     * Excel 导入 数据源本地文件,不返回校验结果 导入 字 段类型 Integer,Long,Double,Date,String,Boolean
     *
     * @param file
     * @param pojoClass
     * @param params
     * @return
     */
    public static <T> List<T> importExcel(File file, Class<?> pojoClass, ImportParams params) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            return new ExcelImportService().importExcelByIs(in, pojoClass, params, false).getList();
        } catch (ExcelImportException e) {
            throw new ExcelImportException(e.getType(), e);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ExcelImportException(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * Excel 导入 数据源IO流,不返回校验结果 导入 字段类型 Integer,Long,Double,Date,String,Boolean
     *
     * @param inputstream
     * @param pojoClass
     * @param params
     * @return
     * @throws Exception
     */
    public static <T> List<T> importExcel(InputStream inputstream, Class<?> pojoClass,
                                          ImportParams params) throws Exception {
        return new ExcelImportService().importExcelByIs(inputstream, pojoClass, params, false).getList();
    }

    /**
     * Excel 导入 数据源IO流 字段类型 Integer,Long,Double,Date,String,Boolean
     * 支持校验,支持Key-Value
     *
     * @param inputstream
     * @param pojoClass
     * @param params
     * @return
     * @throws Exception
     */
    public static <T> ExcelImportResult<T> importExcelMore(InputStream inputstream,
                                                             Class<?> pojoClass,
                                                             ImportParams params) throws Exception {
        return new ExcelImportService().importExcelByIs(inputstream, pojoClass, params, true);
    }

    /**
     * Excel 导入 数据源本地文件 字段类型 Integer,Long,Double,Date,String,Boolean
     * 支持校验,支持Key-Value
     * @param file
     * @param pojoClass
     * @param params
     * @return
     */
    public static <T> ExcelImportResult<T> importExcelMore(File file, Class<?> pojoClass,
                                                             ImportParams params) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            return new ExcelImportService().importExcelByIs(in, pojoClass, params, true);
        } catch (ExcelImportException e) {
            throw new ExcelImportException(e.getType(), e);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ExcelImportException(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }



}
