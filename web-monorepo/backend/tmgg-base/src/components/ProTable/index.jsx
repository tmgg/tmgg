import {message, Table} from 'antd';
import Toolbar from './components/ToolBar';
import React from "react";
import SearchForm from "./components/SearchForm";
import './index.less'

export class ProTable extends React.Component {
    state = {
        selectedRowKeys: [],
        selectedRows: [],

        tableSize: 'small',

        loading: true,
        params: {},
        dataSource: [],


        total: 0,
        current: 1, // 当前页
        pageSize: 10,

        sorter: {
            field: undefined, // 字段
            order: undefined, // 排序 ascend, descend
        },

        // 服务端返回的一些额外数据
        extData:{
            // 总结栏
            summary: null,
            // 自动render相关的，如导出
            autoRenderEnable:false
        }
    }

    constructor(props) {
        super(props);
        if (props.defaultPageSize) {
            this.state.pageSize = props.defaultPageSize
        }
    }

    formRef = React.createRef()

    componentDidMount() {
        this.loadData()

        if (this.props.actionRef) {
            this.props.actionRef.current = {
                reload: () => this.loadData()
            }
        }
    }


    loadData = () => {
        const {request} = this.props
        const params = {...this.state.params}
        params.size = this.state.pageSize
        params.page = this.state.current

        const {sorter} = this.state

        const {field, order} = sorter
        if (field) {
            params.sort = field + "," + (order === 'ascend' ? 'asc' : 'desc')
        }


        this.setState({loading: true})
        request(params).then(rs => {
            const {content, totalElements,extData} = rs;

            this.setState({dataSource: content, total: parseInt(totalElements)})
            if(extData){
                this.setState({extData})
            }
            this.updateSelectedRows(content)

        }).finally(() => {
            this.setState({loading: false})
        })
    }
    exportFile = (type) => {
        const {request} = this.props
        const params = {...this.state.params}
        const {sorter} = this.state

        const {field, order} = sorter
        if (field) {
            params.sort = field + "," + (order === 'ascend' ? 'asc' : 'desc')
        }

        params._exportType = type
        params.size = -1

        const hide = message.loading('下载中...',0)
        request(params).then((r)=>{
           console.log('下载完成(不一定成功)')
        }).finally(hide)

    };

    // 数据重新加载后，更新toolbar需要的已选择数据行
    updateSelectedRows = list => {
        const {rowKey = "id"} = this.props
        const {selectedRows} = this.state
        for (let i = 0; i < selectedRows.length; i++) {
            for (let newItem of list) {
                let oldItem = selectedRows[i];
                if (oldItem[rowKey] === newItem[rowKey]) {
                    selectedRows[i] = newItem;
                    break
                }
            }
        }

        this.setState({selectedRows: [...selectedRows]})
    };

    onFormRef = instance => {
        if (this.props.formRef) {
            this.props.formRef.current = instance
        }
        this.formRef.current = instance
    }

    render() {
        const {
            actionRef,
            toolBarRender,
            columns,
            rowSelection,
            rowKey = "id",
            toolbarOptions,
            searchFormItemsRender,
        } = this.props

        let searchFormNode = null

        if (searchFormItemsRender) {
            searchFormNode = <SearchForm
                formRef={this.onFormRef}
                loading={this.state.loading}
                searchFormItemsRender={searchFormItemsRender}
                onSearch={this.onSearch}
            >
            </SearchForm>
        }

        return <div className='tmgg-pro-table'>
            {toolbarOptions !==false && <Toolbar
                searchFormNode={searchFormNode}
                actionRef={actionRef}
                toolBarRender={this.getToolBarRenderNode(toolBarRender)}
                onRefresh={() => this.loadData()}
                onExport={(type)=>this.exportFile(type)}
                toolbarOptions={toolbarOptions}
                onSearch={this.onSearch}
                loading={this.state.loading}
                params={this.state.params}
                changeFormValues={this.changeFormValues}
                autoRenderEnable={this.state.extData.autoRenderEnable}
            />}


            <Table
                loading={this.state.loading}
                columns={columns}
                dataSource={this.state.dataSource}
                rowKey={rowKey}
                size={this.state.tableSize}
                rowSelection={this.getRowSelectionProps(rowSelection)}
                scroll={{x: 'max-content'}}
                pagination={{
                    showSizeChanger: true,
                    total: this.state.total,
                    pageSize: this.state.pageSize,
                    current: this.state.current,
                    pageSizeOptions: [10, 20, 50, 100, 500, 1000, 5000],
                    showTotal: (total) => `共 ${total} 条`
                }}

                onChange={(pagination, filters, sorter, extra) => {
                    this.setState({
                        current: pagination.current,
                        pageSize: pagination.pageSize,
                        sorter
                    }, this.loadData)
                }}
                summary={(data) => {
                    return this.state.extData.summary
                }}
                bordered={this.props.bordered}
            />
        </div>

    }


    getToolBarRenderNode(toolBarRender) {
        if (!toolBarRender) {
            return
        }
        let {selectedRows, selectedRowKeys, params} = this.state;
        return toolBarRender(params, {
            selectedRows: selectedRows,
            selectedRowKeys: selectedRowKeys,
        });
    }

    getRowSelectionProps = rowSelection => {
        if (rowSelection == null || rowSelection === false) {
            return null
        }
        if (rowSelection === true) {
            rowSelection = {}
        }
        let {type, onChange: inputOnChange} = rowSelection


        return {
            type,
            onChange: (selectedRowKeys, selectedRows) => {
                this.setState({selectedRowKeys, selectedRows})
                if (inputOnChange) {
                    inputOnChange(selectedRowKeys, selectedRows)
                }
            },
            selectedRowKeys: this.state.selectedRowKeys
        };
    };

    onSearch = (values) => {
        this.setState({params: values, current: 1}, this.loadData)
    }

    changeFormValues = (values) => {
        if(this.formRef.current){
            this.formRef.current.resetFields()
            this.formRef.current.setFieldsValue(values)
            this.formRef.current.submit()
        }
    }

}







