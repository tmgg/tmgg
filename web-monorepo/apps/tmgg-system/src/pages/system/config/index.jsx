import {Modal, Table} from 'antd';
import React from 'react';
import {ProTable} from "@ant-design/pro-components";
import {ButtonList, FieldText, HttpClient} from "../../../common";

const baseTitle = "系统配置";
const baseApi = 'sysConfig/';
const basePerm = 'sysConfig:';

const editTitle = '编辑' + baseTitle


const editApi = baseApi + 'edit'
const pageApi = baseApi + 'page'

const editPerm = basePerm + 'edit'
export default class extends React.Component {

  state = {
    showEditForm: false,
    formValues: {},


    list: [],
  }

  columns = [
    {
      title: '参数名称',
      dataIndex: 'name',
      width: 200,
      renderFormItem: () => {
        return <FieldText/>
      }
    },

    {
      title: '唯一编码',
      dataIndex: 'code',
      width: 150,
      renderFormItem: () => {
        return <FieldText/>
      }
    },
    {
      title: '备注',
      dataIndex: 'remark',
      valueType: 'textarea',
      renderFormItem: () => {
        return <FieldText/>
      }
    },
    {
      title: '参数值',
      dataIndex: 'value',
      formItemProps: {
        required: true,
        rules: [{required: true}]
      }
    },



    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      width: 100,
      render: (_, r) => {
        if (r.code == null) {
          return null;
        }
        return <ButtonList>
          <a perm={editPerm} onClick={() => {
            this.setState({
              showEditForm: true,
              formValues: r
            })
          }}> 设置 </a>
        </ButtonList>;
      },
    },
  ];


  componentDidMount() {
    this.loadData()
  }

  loadData = () => {
    HttpClient.get(pageApi).then(rs => {
      const list = rs.data;
      this.setState({list}, this.filterList)
    })
  }
  handleUpdate = value => {
    let params = {id: this.state.formValues.id, ...value}
    HttpClient.post(editApi, params).then(rs => {
      this.setState({
        showEditForm: false
      })
      this.loadData()
    })
  }


  render() {
    return <>

      <Table dataSource={this.state.list} columns={this.columns} pagination={false} rowKey='id' />

      <Modal
        maskClosable={false}
        destroyOnClose
        title={editTitle}
        open={this.state.showEditForm}
        onCancel={() => {
          this.setState({
            showEditForm: false
          })
        }}
        footer={null}
        width={800}
      >
        <ProTable
          onSubmit={this.handleUpdate}
          form={{initialValues: this.state.formValues, layout:'horizontal', labelCol:{flex:'80px'}}}
          type="form"
          columns={this.columns}
        />
      </Modal>


    </>
  }


}



