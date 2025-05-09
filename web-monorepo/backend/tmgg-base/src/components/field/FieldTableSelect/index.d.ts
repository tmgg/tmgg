// @ts-ignore
import React from "react";

declare type FieldTableSelectProps = {
    /**
     * 远程方法连接， 返回的data数据为 io.tmgg.lang.obj.Table
     */
    url:string;

    type: 'checkbox' | 'radio';



    /**
     * 显示的字段
     */
    labelKey: string;

    placeholder?:string;

};

/**
 * 下拉表格
 *
 * 后端参考代码：
 *
 *     @GetMapping("tableSelect")
 *     public AjaxResult tableSelect(String searchText, String selectedKey, Pageable pageable) {
 *         JpaQuery<ApiResource> q = new JpaQuery<>();
 *         q.searchText(searchText, ApiResource.Fields.name, ApiResource.Fields.uri, ApiResource.Fields.desc);
 *         Page<ApiResource> list = apiResourceService.findAll(q,pageable);
 *
 *
 *
 *         Table<ApiResource> tb = new Table<>(list.getContent());
 *         tb.addColumn("标识", "id");
 *         tb.addColumn("名称", ApiResource.Fields.name);
 *         tb.addColumn("路径", ApiResource.Fields.uri);
 *         tb.addColumn("描述", ApiResource.Fields.desc);
 *
 *
 *         if(StrUtil.isNotEmpty(selectedKey)){
 *             boolean containsSelected = tb.getDataSource().stream().anyMatch(t -> t.getId().equals(selectedKey));
 *             if(!containsSelected){
 *                 ApiResource selectedRow = apiResourceService.findOne(selectedKey);
 *                 tb.getDataSource().add(selectedRow);
 *             }
 *         }
 *
 *
 *         return AjaxResult.ok().data(tb);
 *     }
 */
export class FieldTableSelect extends React.Component<FieldTableSelectProps, any> {
}
