import {PlusOutlined} from '@ant-design/icons'
import {Button, Card,InputNumber, Popconfirm,Modal,Form,Input,message} from 'antd'
import React from 'react'
import {ButtonList,dictValueTag, FieldDateRange,FieldDictSelect,FieldRadioBoolean, FieldDatePickerString, FieldDateTimePickerString, HttpUtil, ProTable} from "@tmgg/tmgg-base";



export default class extends React.Component {


  tableRef = React.createRef()

  columns = [

    {
      title: '商户退款单号',
      dataIndex: 'outRefundNo',




    },

    {
      title: '商户订单号',
      dataIndex: 'outTradeNo',




    },

    {
      title: '退款成功时间',
      dataIndex: 'successTime',




    },

    {
      title: '退款状态',
      dataIndex: 'status',




    },

    {
      title: '金额',
      dataIndex: 'amount',




    },

      {
          title: '操作',
          dataIndex: 'option',
          render:(_, record)=> {
              return <ButtonList>
                  <Button size='small' onClick={()=>this.handleQuery(record)}>查询状态</Button>
                  <Button size='small' onClick={()=>this.triggerNotify(record)}>触发通知</Button>
              </ButtonList>;
          }
      },
  ]

    handleQuery = record => {
        HttpUtil.get( 'paymentRefundOrder/queryOrder', {id:record.id}).then(rs => {
            this.tableRef.current.reload()
        })
    }
    triggerNotify = record => {
        HttpUtil.get( 'paymentRefundOrder/triggerNotify', {id:record.id}).then(rs => {
            this.tableRef.current.reload()
        })
    }


    render() {
    return <>
      <ProTable
          actionRef={this.tableRef}
          request={(params, sort) => HttpUtil.pageData('paymentRefundOrder/page', params)}
          columns={this.columns}
      />




    </>


  }
}


