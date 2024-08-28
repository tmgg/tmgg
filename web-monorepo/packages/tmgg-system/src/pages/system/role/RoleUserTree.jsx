import {Button, Divider, message, Tree} from 'antd';
import React from 'react';
import {HttpClient} from "../../../common";
import {PageLoading} from "@ant-design/pro-components";


export default class extends React.Component {


  state = {
    treeData: null,
    checked: [],
    confirmLoading: false,
  }
  actionRef = React.createRef();

  componentDidMount() {
    // 加载树状菜单
    HttpClient.get('/sysUser/tree').then(rs => {
      const list = rs.data;
      this.setState({treeData: list})
    })


    const hide = message.loading('加载中...', 0)
    HttpClient.get('/sysRole/getUserIdsByRoleId', {roleId: this.props.id}).then(rs => {
      const ids = rs.data;
      this.setState({checked: ids})
      hide()
    })
  }

  handleSave = () => {
    const data = {
      roleId: this.props.id,
      checkedUserIds: this.state.checked
    }
    this.setState({
      confirmLoading: true
    })
    HttpClient.post('sysRole/grantToUser', data).then(rs => {
      this.setState({
        confirmLoading: false
      })
      message.success(rs.message)
    })
  }

  componentDidUpdate(prevProps, prevState, snapshot) {
    if (prevProps.id != this.props.id) {
      this.onRoleIdChange(this.props.id)
    }

  }


  onCheck = (ids) => {
    this.setState({checked: ids})
  }

  render() {
    let {treeData, confirmLoading, checked} = this.state

    if (treeData == null) {
      return <PageLoading/>
    }

    return <>
      <Tree
        treeData={treeData}
        multiple
        checkable
        checkedKeys={checked}
        onCheck={this.onCheck}
        defaultExpandAll
      >
      </Tree>
      <Divider/>
      <Button onClick={this.handleSave} loading={confirmLoading} type='primary'>保存用户</Button>
    </>
  }


}



