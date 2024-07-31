import React from "react";
import {Button, Form, Input, message, Table} from "antd";
import {libs} from "./ValueType";
import {ReloadOutlined} from "@ant-design/icons";
import './index.less'
import hutool from "@moon-cn/hutool";
import {sys} from "../../system";

export class ZzTable extends React.Component {

  static defaultProps = {
    search: true
  }

  componentDidMount() {
    this.reload()
    this.columns = this.props.columns.filter(c => !c.hideInTable)
    for (let col of this.columns) {
      if (col.valueType != null && col.render == null) {
        col.render = (value, record) => {
          let props = {params: col.params};
          return libs[col.valueType]?.render(value, props)
        }
      }
    }
  }

  state = {

    dataSource: [],
    dataSourceLoading: false,

    pagination: {
      current: 1,
      pageSize: 10,
      total: 0
    },
    sorter: {
      field: null,
      sorter: null //
    }
  }

  columns = []
  formRef = React.createRef()

  reload = (pageNo) => {
    const {requestUrl} = this.props
    if (requestUrl == null) {
      message.error("请定义requestUrl")
      return
    }

    this.setState({dataSourceLoading: true})

    let data = {}
    if (this.props.search) {
      data = this.formRef.current.getFieldsValue()
    }


    const {pagination, sorter} = this.state
    const params = {
      pageNo: pageNo || pagination.current,
      pageSize: pagination.pageSize,
    }
    if(sorter && sorter.field){
      params.orderBy =  sorter.field + ',' + (sorter.order === 'ascend' ? 'asc' : 'desc');
    }

    hutool.http.post(sys.getServerUrl() + requestUrl, data, params).then(rs => {
      const dataSource = rs.data.content

      const pagination = {
        total: parseInt(rs.data.totalElements),
        pageSize: rs.data.size,
        current: rs.data.number + 1
      }
      this.setState({dataSource, pagination, dataSourceLoading: false})
    }).catch(e => {
      this.setState({dataSourceLoading: false})
    })
  }

  onTableChange = (pagination, filters, sorter) => {
    this.state.pagination = pagination
    this.state.sorter = sorter
    console.log(sorter)

    this.reload()
  }

  render() {
    const {type} = this.props;
    if (type === 'form') {
      return 'zzTable 不支持 type:form'
    }

    return (
      <section className='zz-table'>
        {this.renderSearch()}

        <div className='content'>


          {this.renderToolbar()}
          <Table
            columns={this.columns}
            loading={this.state.dataSourceLoading}
            dataSource={this.state.dataSource}
            size={this.props.size || 'middle'}
            bordered={this.props.bordered == null ? true : this.props.bordered}
            rowKey={this.props.rowKey || 'id'}
            onChange={this.onTableChange}
            pagination={this.state.pagination}
          ></Table></div>
      </section>
    );
  }

  renderSearch = () => {
    if (this.props.search === false || this.props.columns == null) {
      return null
    }
    const cols = this.props.columns.filter(col => !col.hideInSearch && col.valueType !== 'option')
    return <div className='search'>
      <Form ref={this.formRef} layout='inline' onFinish={() => {
        this.reload(1);
      }}>
        {cols.map(col => {
          return <Form.Item name={col.dataIndex} label={col.title} key={col.dataIndex}>
            {libs[col.valueType] != null ? libs[col.valueType].renderFormItem(null, {params: col.params}) : <Input/>}
          </Form.Item>
        })}
        <div className='actions'>
          <Button type='primary' htmlType='submit' loading={this.state.dataSourceLoading}>查询</Button></div>
      </Form>
    </div>;
  };

  renderToolbar = () => {
    const selectedRows = 0

    let custom = null
    if (this.props.toolBarRender) {
      custom = this.props.toolBarRender(null, {selectedRows})
    }

    return (
      <div className='toolbar'>
        {custom}
        <ReloadOutlined onClick={()=>this.reload()} title='刷新'/>
      </div>
    )
  };
}
