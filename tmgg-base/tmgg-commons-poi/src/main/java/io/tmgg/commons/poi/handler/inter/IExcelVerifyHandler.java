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
package io.tmgg.commons.poi.handler.inter;

import io.tmgg.commons.poi.excel.entity.result.ExcelVerifyHandlerResult;

/**
 * 导入校验接口
 *
 * @author JueYue
 *  2014年6月23日 下午11:08:21
 */
public interface IExcelVerifyHandler<T> {

    /**
     * 导入校验方法
     *
     * @param obj
     *            当前对象
     * @return
     */
    public ExcelVerifyHandlerResult verifyHandler(T obj);

}
