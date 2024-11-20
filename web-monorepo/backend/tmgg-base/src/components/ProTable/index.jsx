import {Card, Table} from 'antd';
import Toolbar from './components/ToolBar';
import React from "react";


export class ProTable extends React.Component {
    state = {
        selectedRowKeys: [],
        selectedRows: [],

        tableSize: 'small',

        loading: true,
        params: {},
        dataSource: [],

        keyword: null, // 搜索input的值

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
    }


    loadData = () => {
        const {request} = this.props
        const params = {...this.state.params}
        params.size = this.state.pageSize
        params.page = this.state.current
        params.keyword = this.state.keyword

        const {sorter} = this.state

        const {field, order} = sorter
        if(field){
            params.sort = field + "," + (order === 'ascend' ? 'asc' : 'desc')
        }


        this.setState({loading:true})
        request(params).then(rs => {
            const {content, totalElements} = rs;
            this.setState({dataSource: content, total: parseInt(totalElements)})
        }).finally(()=>{
            this.setState({loading:false})
        })
    }



    render() {
        const {
            actionRef,
            toolBarRender,
            request,
            columns,
            rowSelection,
            rowKey = "id",
            headerTitle,
            searchInput = false
        } = this.props

        return <div>
            <Card styles={{
                body: {paddingTop: 0}
            }}
            >
                <Toolbar
                    headerTitle={headerTitle}

                    actionRef={actionRef}

                    toolBarRender={this.getToolBarRenderNode(toolBarRender)}

                    onTableSizeChange={tableSize => this.setState({tableSize})}
                    onRefresh={() => this.loadData()}

                    searchInput={searchInput}
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
                        pageSizeOptions: [10, 20, 50, 100, 500, 1000, 5000]
                    }}

                    onChange={(pagination, filters, sorter, extra) => {
                        this.setState({current: pagination.current, pageSize: pagination.pageSize, sorter}, this.loadData)
                    }}


                />
            </Card>
        </div>

    }


    getToolBarRenderNode(toolBarRender) {
        if (!toolBarRender) {
            return
        }
        return toolBarRender(null, {
            rows: {
                selectedRows: this.state.selectedRows,
                selectedRowKeys: this.state.selectedRowKeys
            }
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

    onSearch = (value)=>{
        this.setState({keyword:value}, this.loadData)
    }

}







