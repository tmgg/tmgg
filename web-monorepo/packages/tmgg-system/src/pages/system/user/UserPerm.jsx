import {Form, Input, Modal, Spin, Tree} from 'antd';
import React from 'react';
import {FieldDictSelect, FieldRemoteMultipleSelect, HttpClient} from "../../../common";


export default class UserPerm extends React.Component {


    state = {
        visible: false,

        confirmLoading: false,

        formValues: {
            dataPermType: null
        },
    }


    handleSave = (values) => {
        values.grantOrgIdList = this.state.checked

        this.setState({
            confirmLoading: true
        })


        HttpClient.post('sysUser/grantPerm', values).then(rs => {
            this.setState({
                visible: false,
                confirmLoading: false
            })
            this.props.onOk()
        })


    }

    show(item) {
        this.setState({visible: true})

        HttpClient.get('/sysUser/getPermInfo', {id: item.id}).then(rs => {
            this.setState({formValues: rs.data})
            this.formRef.current.setFieldsValue(rs.data)
        })


    }

    formRef = React.createRef()


    render() {
        let {visible, treeData, confirmLoading, checked} = this.state

        return <Modal
            title="授权"
            destroyOnClose
            width={600}
            open={visible}
            confirmLoading={confirmLoading}
            onCancel={() => this.setState({visible: false})}
            onOk={() => this.formRef.current.submit()}
        >

            <Form ref={this.formRef}
                  onFinish={this.handleSave}
                  onValuesChange={(change, values) => {
                      this.setState({formValues: values})
                  }}
                  labelCol={{flex: '100px'}}
            >
                <Form.Item name='id' noStyle></Form.Item>
                <Form.Item label='角色' name='roleIds' rules={[{required: true}]}>
                    <FieldRemoteMultipleSelect url='sysRole/options'></FieldRemoteMultipleSelect>
                </Form.Item>
                <Form.Item label='数据权限' name='dataPermType' rules={[{required: true}]}>
                    <FieldDictSelect typeCode='dataPermType'></FieldDictSelect>
                </Form.Item>


                {this.state.formValues.dataPermType === 'CUSTOM' && <>
                    <Form.Item label='组织机构' name='orgIds'>
                        <FieldTree/>
                    </Form.Item>
                </>}


            </Form>


        </Modal>
    }


}

class FieldTree extends React.Component {

    state = {
        treeLoading: true,
        treeData: [],
    }

    componentDidMount() {
        HttpClient.get('/sysOrg/tree').then(rs => {
            const list = rs.data;
            this.setState({treeData: list, treeLoading: false})
        })
    }


    render() {
        if (this.state.treeLoading) {
            return <Spin />
        }
        return <Tree
            multiple
            checkable
            onCheck={e => this.props.onChange(e.checked)}
            checkedKeys={this.props.value}
            treeData={this.state.treeData}
            defaultExpandAll
            checkStrictly
        >
        </Tree>
    }
}




