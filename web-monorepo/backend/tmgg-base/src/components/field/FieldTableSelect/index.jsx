import React from "react";
import {Button, Input, Select, Table} from "antd";
import {HttpUtil} from "../../../system";
import {SearchOutlined} from "@ant-design/icons";

/**
 * 下拉表格
 */
export class FieldTableSelect extends React.Component {

    state = {
        loading:false,
        columns: [],
        dataSource: [],

        open: false,
        searchText: null,

        selectedRow: {}
    }

    componentDidMount() {
        this.loadData()
    }

    loadData(searchText) {
        this.setState({loading:true})
        HttpUtil.get(this.props.url, {searchText}).then(rs => {
            this.setState(rs)
        }).finally(()=>{
            this.setState({loading:false})
        })
    }


    onSearch = searchText => {
        this.setState({searchText})
        this.loadData(searchText)
    };
    labelRender = ({value}) => this.state.selectedRow.name;

    render() {
        return <>
            <Select popupRender={this.popupRender} loading={this.state.loading}
                    open={this.state.open}
                    onOpenChange={visible => this.setState({open: visible})}
                    style={{minWidth: 800}}
                    value={this.state.selectedRow.id}
                    labelRender={this.labelRender}
                    popupMatchSelectWidth={900}
                    placeholder={this.props.placeholder|| '请选择'}
            />
        </>
    }

    onSelect(record) {

    }

    popupRender = () => {
        return <div >
            <div style={{padding: 8, display:'flex',justifyContent:'end'}}>
                <Input
                    placeholder="搜索..."
                    prefix={<SearchOutlined/>}
                    value={this.state.searchText}
                    onChange={(e) => this.onSearch(e.target.value)}
                    style={{maxWidth:280}}
                />
            </div>
            <Table dataSource={this.state.dataSource} loading={this.state.loading}
                   columns={this.state.columns}
                   size='small'
                   bordered
                   rowKey='id'
                   onRow={(record) => {
                       return {
                           onDoubleClick: (event) => {
                               this.setState({selectedRow:record, open: false})
                           },
                       }
                   }}
            ></Table>

        </div>
    };
}
