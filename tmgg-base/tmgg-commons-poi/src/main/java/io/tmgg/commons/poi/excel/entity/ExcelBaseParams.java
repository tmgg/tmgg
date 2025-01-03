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
package io.tmgg.commons.poi.excel.entity;

import io.tmgg.commons.poi.handler.inter.ICommentHandler;
import io.tmgg.commons.poi.handler.inter.IExcelDataHandler;
import io.tmgg.commons.poi.handler.inter.IExcelDictHandler;
import io.tmgg.commons.poi.handler.inter.II18nHandler;
import lombok.Data;

/**
 * 基础参数
 * @author JueYue
 *  2014年6月20日 下午1:56:52
 */
@Data
public class ExcelBaseParams {

    /**
     * 数据处理接口,以此为主,replace,format都在这后面
     */
    private IExcelDataHandler dataHandler;

    /**
     * 字段处理类
     */
    private IExcelDictHandler dictHandler;
    /**
     * 国际化处理类
     */
    private II18nHandler i18nHandler;
    /**
     * 批注处理类
     */
    private ICommentHandler commentHandler;

}
