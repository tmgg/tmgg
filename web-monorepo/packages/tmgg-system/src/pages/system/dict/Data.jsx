import {PlusOutlined} from '@ant-design/icons';
import {Button, Modal, Popconfirm, Radio, Tag} from 'antd';
import React from 'react';
import {ProTable} from "@ant-design/pro-components";
import {ButtonList, hasPermission, HttpClient} from "../../../common";

const baseTitle = "字典数据";
const baseApi = 'sysDictItem/';
const basePerm = 'sysDictItem:';

const addTitle = "添加" + baseTitle
const editTitle = '编辑' + baseTitle
const deleteTitle = '删除' + baseTitle


const addApi = baseApi + 'add'
const editApi = baseApi + 'edit'
const delApi = baseApi + 'delete'
const pageApi = baseApi + 'page'

const addPerm = basePerm + 'add'
const delPerm = basePerm + 'delete'
const editPerm = basePerm + 'edit'

export default class Data extends React.Component {

  state = {
    showAddForm: false,
    showEditForm: false,
    formValues: {},


  }
  actionRef = React.createRef();

  columns = [
    {
      title: '编码',
      dataIndex: 'code'
    },
    {
      title: '字典值',
      dataIndex: 'value'
    },

    {
      title: '排序',
      dataIndex: 'sort'
    },
    {
      title: ' 显示颜色',
      dataIndex: 'color',
      width: 200,
      render(v){
        if(v){
          return <Tag color={v}>{v}</Tag>
        }
      },
      renderFormItem(v) {
        return <Radio.Group>
          <Radio value='success'>success</Radio>
          <Radio value='processing'>processing</Radio>
          <Radio value='error'>error</Radio>
          <Radio value='warning'>warning</Radio>
          <Radio value='default'>default</Radio>
        </Radio.Group>
      }
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => (
        <ButtonList>
          <a perm={editPerm} onClick={() => {
            this.setState({
              showEditForm: true,
              formValues: record
            })
          }}> 修改 </a>


          <Popconfirm perm={delPerm} title={'是否确定' + deleteTitle} onConfirm={() => this.handleDelete(record)}>
            <a>删除</a>
          </Popconfirm>
        </ButtonList>
      ),
    },
  ];
  handleSave = value => {
    value.typeId = this.props.id
    HttpClient.post(addApi, value).then(rs => {
      this.setState({
        showAddForm: false
      })
      this.actionRef.current.reload();
    })
  }

  handleUpdate = value => {
    value.typeId = this.props.id
    let params = {id: this.state.formValues.id, ...value}
    HttpClient.post(editApi, params).then(rs => {
      this.setState({
        showEditForm: false
      })
      this.actionRef.current.reload();
    })
  }

  handleDelete = row => {
    HttpClient.post(delApi, row).then(rs => {
      this.actionRef.current.reload();
    })
  }

  componentDidUpdate(prevProps, prevState, snapshot) {
    this.actionRef.current.reload()
  }

  render() {
    let {showAddForm, showEditForm} = this.state

    return <div>
      <ProTable
        actionRef={this.actionRef}
        search={false}
        options={false}
        toolBarRender={(action, {selectedRows}) => {
          const menus = []

          if (hasPermission(addPerm)) {
            menus.push(<Button type="primary" onClick={() => {
              this.setState({
                showAddForm: true
              })
            }}>
              <PlusOutlined/> 新增
            </Button>)

          }
          return menus
        }}
        request={(params, sort) => {
          params.typeId = this.props.id
          return HttpClient.getPageableData(pageApi, params, sort)
        }
        }
        columns={this.columns}
        rowSelection={false}
        columnEmptyText={false}
        rowKey="id"
      />


      <Modal
        maskClosable={false}
        destroyOnClose
        title={addTitle}
        visible={showAddForm}
        onCancel={() => {
          this.setState({
            showAddForm: false
          })
        }}
        footer={null}
        width={800}
      >
        <ProTable
          onSubmit={this.handleSave}
          form={{layout: 'horizontal', labelCol: {span: 3}}}
          type="form"
          columns={this.columns}
          rowSelection={{}}
        />
      </Modal>


      <Modal
        maskClosable={false}
        destroyOnClose
        title={editTitle}
        open={showEditForm}
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
          form={{initialValues: this.state.formValues, layout: 'horizontal', labelCol: {span: 3}}}
          type="form"
          columns={this.columns}
        />
      </Modal>


    </div>
  }


}



