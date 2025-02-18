import {Card, Table} from 'antd';
import Toolbar from './components/ToolBar';
import React from "react";
import SearchForm from "./components/SearchForm";


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
        }
    }

    componentDidMount() {
        this.loadData()

        if (this.props.actionRef) {
            this.props.actionRef.current = {
                reload: () => this.loadData()
            }
        }

        {
            // 兼容提示代码
            const disabledKeys = ['hideTable', 'hideInForm', 'hideInSearch', 'valueType']
            for (let column of this.props.columns) {
                for (let key in disabledKeys) {
                    if (column[key] != null) {
                        console.error('组件不再支持' + key)
                    }
                }
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
            const {content, totalElements, extInfo} = rs;
            this.setState({dataSource: content, total: parseInt(totalElements), extInfo})
        }).finally(() => {
            this.setState({loading: false})
        })
    }


    render() {
        const {
            actionRef,
            toolBarRender,
            columns,
            rowSelection,
            rowKey = "id",
            showSearch,
            searchFormItemsRender,
            formRef
        } = this.props

        let searchFormNode = null

        if (searchFormItemsRender) {
            searchFormNode = <SearchForm
                formRef={formRef}
                loading={this.state.loading}
                searchFormItemsRender={searchFormItemsRender}
                onSearch={this.onSearch}
            >
            </SearchForm>
        }

        return <div>
            {searchFormNode}

            <Card styles={{
                body: {paddingTop: 0}
            }}
            >
                <Toolbar
                    actionRef={actionRef}
                    toolBarRender={this.getToolBarRenderNode(toolBarRender)}
                    onTableSizeChange={tableSize => this.setState({tableSize})}
                    onRefresh={() => this.loadData()}
                    showSearch={showSearch == null ? (searchFormNode == null) : showSearch} // 未设置搜索表单的情况下，默认显示搜索Input
                    onSearch={this.onSearch}
                />


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
                    summary={(data)=>{
                        return this.state.extInfo
                    }}
                    bordered={this.props.bordered}
                />
            </Card>
        </div>

    }


    getToolBarRenderNode(toolBarRender) {
        if (!toolBarRender) {
            return
        }
        return toolBarRender(this.state.params, {
                selectedRows: this.state.selectedRows,
                selectedRowKeys: this.state.selectedRowKeys
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
        this.setState({params:values, current: 1}, this.loadData)
    }

}







