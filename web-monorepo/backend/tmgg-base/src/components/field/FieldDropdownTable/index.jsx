import React from "react";
import {DatePicker, Table} from "antd";
import dayjs from "dayjs";
import {HttpUtil} from "../../../system";

/**
 * 下拉表格
 */
export class FieldDropdownTable extends React.Component {

    state = {
        columns:[],
        dataSource:[]
    }
    componentDidMount() {
        this.loadData()
    }

    loadData(){
        HttpUtil.get(this.props.url).then(rs=>{
            this.setState(rs)
        })
    }




    render() {
        return <>

            <Table dataSource={this.state.dataSource}
                   columns={this.state.columns}
                    size='small'
                   bordered
                   rowSelection={{}}
            ></Table>
        </>
    }

}
