/**
 * 工具栏
 */
import {HistoryOutlined, ReloadOutlined} from '@ant-design/icons';
import {Button, Input, message, Modal, Table} from 'antd';
import React from 'react';
import './index.less';
import {DateUtil, StorageUtil} from "@tmgg/tmgg-commons-lang";
import {PageUtil} from "../../../../system";
import excel from './excel.svg'

export default class Toolbar extends React.Component {

    state = {
        historyOpen: false
    }

    render = () => {
        const {
            onExportExcel,
            onRefresh,
            showSearch,
            toolBarRender,
            loading,
            searchFormNode,
            params
        } = this.props;


        return <div className='pro-table-toolbar'>

            <div className='pro-table-toolbar-search'>
                {showSearch && <Input.Search
                    style={{width: 200}}
                    placeholder='搜索...'
                    onSearch={(v) => this.props.onSearch({keyword: v})}
                />
                }

                {searchFormNode}
            </div>

            <div className='pro-table-toolbar-option'>
                {toolBarRender}
                <button title='导出EXCEL'  className='btn' onClick={onExportExcel}>
                    <img src={excel} />
                </button>

                <Button icon={<ReloadOutlined/>} shape="circle" onClick={onRefresh} title='刷新' loading={loading}/>
                <Button icon={<HistoryOutlined/>} shape="circle" onClick={this.onClickHistory} title='查询历史'/>
            </div>

            {this.renderHistory()}
        </div>
    };

    renderHistory() {
        const {params} = this.props
        const list = StorageUtil.get(this.getParamKey()) || []

        const dataSource = [{params,time:'当前'}, ...list]

        return <Modal title='查询方案'
                      width={800}
                      open={this.state.historyOpen}
                      onCancel={() => this.setState({historyOpen: false})}
                      footer={null}
                      destroyOnHidden
        >

            <Table
                dataSource={dataSource}
                pagination={false}
                columns={[
                    {
                        dataIndex: 'time', title: '时间'
                    },
                    {
                        dataIndex: 'params', title: '参数',
                        render(v) {
                            return JSON.stringify(v)
                        }
                    }, {
                        dataIndex: 'option', title: '-',
                        render: (v, record) => {
                            if (record.time ==='当前') {
                                return <Button onClick={this.onSaveHistory}>保存</Button>
                            }
                            return <Button type='primary' onClick={() => this.onApply(record.params)}>使用</Button>
                        }

                    }
                ]}></Table>


        </Modal>;
    }

    onClickHistory = () => {

        this.setState({historyOpen: true})
    }

    getParamKey() {
        return 'query-params-' + PageUtil.currentPathname();
    }

    onSaveHistory = () => {
        const {params} = this.props
        const keys = Object.keys(params)
        if (keys.length === 0) {
            message.error('查询参数为空，无法保存')
            return
        }
        const list = StorageUtil.get(this.getParamKey()) || []
        let data = {time: DateUtil.now(), params};
        list.unshift(data)
        if (list.length > 5) {
            list.pop()
        }
        StorageUtil.set(this.getParamKey(), list)
        message.success('保存成功')
        this.setState({historyOpen:false})
    }

    onApply = (params) => {
        this.props.changeFormValues(params)
        this.setState({historyOpen:false})
    }
}


