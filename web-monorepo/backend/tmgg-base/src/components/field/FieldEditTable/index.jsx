import {Button, Form, Input, message, Table} from 'antd'
import React from 'react'
import {DeleteOutlined, PlusOutlined} from "@ant-design/icons";
import './index.less'
import {ArrUtil} from "@tmgg/tmgg-commons-lang";

export class FieldEditTable extends React.Component {


    columns = []


    constructor(props) {
        super(props);

        this.columns = this.props.columns.map(col => {
            if (col.render == null) {
                col.render = (v, record,index) => {
                    return  <Input value={v} onChange={(e)=>this.onCellChange(index,col.dataIndex,e)}/>
                }
            }else {
                if(!col._oldRender){
                    col._oldRender = col.render
                    col.render = (v, record,index) => {
                        const cmp =col._oldRender()
                        return React.createElement(cmp.type,{...cmp.props, value:v, onChange: (e)=>this.onCellChange(index, col.dataIndex, e)})
                    }
                }
            }
            return col
        })

        this.columns.push({
            title: '/',
            render:(v, record) =>{
                return <Button icon={<DeleteOutlined/>} size='small' shape={'circle'}
                               onClick={() => this.remove(record)}></Button>
            }
        })

        if (this.props.value != null) {
            this.state.dataSource = this.props.value
        }

    }

    state = {
        dataSource: []
    }


    onCellChange=(index,dataIndex,e)=>{
        let {dataSource} = this.state
        let row =dataSource[index]

        let v = e;
        if(e.hasOwnProperty('target')){
            v = e.target.value;
        }


        row[dataIndex] = v

        dataSource = [...dataSource]
        this.setState({dataSource},this.notifyParent)
    }

    add = () => {
        let {dataSource} = this.state
        dataSource = [...dataSource, {}];
        this.setState({dataSource},this.notifyParent)
    };
    remove = (record) => {
        let {dataSource} = this.state
        ArrUtil.remove(dataSource, record)
        this.setState({dataSource:[...dataSource]},this.notifyParent)
    };

    notifyParent(){
        let {dataSource} = this.state
        this.props.onChange(dataSource)
    }

    render() {
        return <div className='edit-table'>

            <Table columns={this.columns}
                   dataSource={this.state.dataSource}
                   size='small'

                   footer={() => <Button type='dashed' icon={<PlusOutlined/>}
                                         onClick={this.add}>增加一行
                   </Button>}
            >

            </Table>


        </div>
    }
}
