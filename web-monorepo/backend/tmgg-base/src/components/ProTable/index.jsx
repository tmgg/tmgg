import {Button, Form, Input, Table} from 'antd';
import Toolbar from './components/ToolBar';
import React from "react";
import './index.less'
import {StrUtil} from "@tmgg/tmgg-commons-lang";
import {SearchOutlined} from "@ant-design/icons";


function getDefaultPageSize() {
    const h = window.screen.height;
    if (h >= 1080) {
        return 20;
    }
    if (h >= 768) {
        return 15;
    }
    return 10;
}

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
        pageSize: getDefaultPageSize(),

        sorter: {
            field: undefined, // 字段
            order: undefined, // 排序 ascend, descend
        },

        // 服务端返回的一些额外数据
        extData: {
            // 总结栏
            summary: null,
        },

        scrollY: null
    }

    showSearch = true

    constructor(props) {
        super(props);
        if (props.defaultPageSize) {
            this.state.pageSize = props.defaultPageSize
        }
        this.id = StrUtil.random(32)
        this.showSearch = this.props.showSearch == null ? true : this.props.showSearch;
    }

    formRef = React.createRef()

    componentDidMount() {
        this.loadData()
        if (this.props.actionRef) {
            this.props.actionRef.current = {
                reload: () => this.loadData()
            }
        }

        let scrollY = this.props.scrollY;
        if (scrollY) {
            this.setState({scrollY: scrollY})
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
            const {content, totalElements, extData} = rs;

            this.setState({dataSource: content, total: parseInt(totalElements)})
            if (extData) {
                this.setState({extData})
            }
            this.updateSelectedRows(content)

        }).finally(() => {
            this.setState({loading: false})
        })
    }

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


    render() {
        const {
            actionRef,
            toolBarRender,
            columns,
            rowSelection,
            rowKey = "id",
            toolbarOptions,
        } = this.props


        return <div className={'tmgg-pro-table '} id={this.id}>

            {this.renderForm()}


            {toolbarOptions !== false && <Toolbar
                actionRef={actionRef}
                toolBarRender={this.getToolBarRenderNode(toolBarRender)}

                onRefresh={() => this.loadData()}
                toolbarOptions={toolbarOptions}
                onSearch={this.onSearch}
                loading={this.state.loading}
                params={this.state.params}
                changeFormValues={this.changeFormValues}
            />}


            <Table
                loading={this.state.loading}
                columns={columns}
                dataSource={this.state.dataSource}
                rowKey={rowKey}
                size={this.state.tableSize}
                rowSelection={this.getRowSelectionProps(rowSelection)}
                scroll={{x: 'max-content', y: this.state.scrollY}}
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

                footer={()=>{
                    return this.state.extData.summary
                }}
                bordered={this.props.bordered}


            />
        </div>

    }


    renderForm = () => {
        let showSearch = this.showSearch;
        if(this.props.children || this.props.searchFormItemsRender){
            showSearch = false
        }
        return <Form
            layout="inline"
            onFinish={(values) => this.onSearch(values)}
            ref={this.formRef}
            style={{gap: '8px 4px',marginBottom:12}}
            labelCol={{flex:'70px'}}
        >

            {showSearch && <Form.Item name='searchText'>
                <Input style={{width: 200}} placeholder='搜索...'/>
            </Form.Item>}

            {this.props.searchFormItemsRender && this.props.searchFormItemsRender(this.formRef.current)}
            {this.props.children}

            <Form.Item>
                <Button type='primary'  htmlType="submit" icon={<SearchOutlined/>}> 查询
                </Button>
            </Form.Item>
        </Form>;
    };

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
        if (this.formRef.current) {
            this.formRef.current.resetFields()
            this.formRef.current.setFieldsValue(values)
            this.formRef.current.submit()
        }
    }

}







