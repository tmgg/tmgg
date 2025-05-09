import React from "react";
import {Input, Select, Table} from "antd";
import {HttpUtil} from "../../../system";
import {SearchOutlined} from "@ant-design/icons";

/**
 * 下拉表格
 */
export class FieldTableSelect extends React.Component {

    state = {
        loading: false,
        columns: [],
        dataSource: [],

        open: false,
        searchText: null,

        value: null,
        label: null,

    }

    constructor(props) {
        super(props);
        this.state.value = props.value
    }

    componentDidMount() {
        this.loadData()
    }

    loadData(searchText) {
        this.setState({loading: true})
        HttpUtil.get(this.props.url, {searchText, selectedKey: this.state.value}).then(rs => {
            this.setState(rs)

            if (this.state.label == null) {
                const selectedRow = rs.dataSource.find(t=>t.id === this.state.value)
                if(selectedRow){
                    this.setState({label:selectedRow[this.props.labelKey]})
                }


            }

        }).finally(() => {
            this.setState({loading: false})
        })
    }


    onSearch = searchText => {
        this.setState({searchText})
        this.loadData(searchText)
    };

    render() {
        return <>
            <Select popupRender={this.popupRender} loading={this.state.loading}
                    open={this.state.open}
                    onOpenChange={visible => this.setState({open: visible})}
                    style={{minWidth: 300}}
                    value={this.state.value}
                    labelRender={() => this.state.label}
                    popupMatchSelectWidth={900}
                    placeholder={this.props.placeholder || '请选择'}
            />
        </>
    }


    popupRender = () => {
        return <div>
            <div style={{padding: 8, display: 'flex', justifyContent: 'end'}}>
                <Input
                    placeholder="搜索..."
                    prefix={<SearchOutlined/>}
                    value={this.state.searchText}
                    onChange={(e) => this.onSearch(e.target.value)}
                    style={{maxWidth: 280}}
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
                               let value = record.id;
                               let label = record[this.props.labelKey]

                               this.setState({label, value, selectedRow: record, open: false})
                               this.props.onChange(value)
                           },
                       }
                   }}
            ></Table>

        </div>
    };
}
