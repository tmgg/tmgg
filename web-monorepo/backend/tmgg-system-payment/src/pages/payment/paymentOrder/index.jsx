import {PlusOutlined} from '@ant-design/icons'
import {Button, Card,InputNumber, Popconfirm,Modal,Form,Input,message} from 'antd'
import React from 'react'
import {ButtonList,dictValueTag, FieldDateRange,FieldDictSelect,FieldRadioBoolean, FieldDatePickerString, FieldDateTimePickerString, HttpUtil, ProTable} from "@tmgg/tmgg-base";



export default class extends React.Component {


  tableRef = React.createRef()

  columns = [

    {
      title: '用户',
      dataIndex: 'openId',
    },
    {
      title: '商户订单号',
      dataIndex: 'outTradeNo',
    },
    {
      title: '金额（分）',
      dataIndex: 'amount',
    },
    {
      title: '订单描述',
      dataIndex: 'description',
    },
    {
      title: '渠道编码',
      dataIndex: 'paymentChannelCode',
    },
    {
      title: '渠道名称',
      dataIndex: 'paymentChannelFullname',
      render(_, record){
        return record.paymentChannelName || record.paymentChannelFullname
      }
    },

    {
      title: '状态',
      dataIndex: 'status',

    },

    {
      title: '操作',
      dataIndex: 'option',
      render:(_, record)=> {
        return <ButtonList>
          <Button size='small' onClick={()=>this.handleQuery(record)}>查询状态</Button>
          <Button size='small' onClick={()=>this.triggerNotify(record)}>触发通知</Button>
          <Button size='small' onClick={()=>this.refund(record)}>退款</Button>
        </ButtonList>;
      }
    },
  ]






  handleQuery = record => {
    HttpUtil.get( 'paymentOrder/queryOrder', {id:record.id}).then(rs => {
      this.tableRef.current.reload()
    })
  }
  triggerNotify = record => {
    HttpUtil.get( 'paymentOrder/triggerNotify', {id:record.id}).then(rs => {
      this.tableRef.current.reload()
    })
  }

  refund = record => {
    HttpUtil.get( 'paymentOrder/refund', {id:record.id}).then(rs => {
      this.tableRef.current.reload()
    })
  }

  render() {
    return <>
      <ProTable
          actionRef={this.tableRef}
          request={(params, sort) => HttpUtil.pageData('paymentOrder/page', params)}
          columns={this.columns}
      />


    </>


  }
}


