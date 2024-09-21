import {Button, Divider, message, Tree} from 'antd';
import React from 'react';
import {http, HttpUtil} from "@tmgg/tmgg-base";


export default class extends React.Component {


  state = {
    treeData:  null,
    checked: [],
    confirmLoading: false,
  }
  actionRef = React.createRef();

  componentDidMount() {
    const {id} = this.props;
    HttpUtil.get('/sysMenu/treeForGrant').then(rs => {
      this.setState({treeData: rs})
    })

    this.init(id);
  }

  init = id => {
    this.setState({ checked: []})

    // 加载关联关系
    const hide = message.loading('加载中...', 0)
    HttpUtil.get('/sysRole/ownMenu', {id: id}).then(ids => {
      this.setState({checked: ids})
      hide()
    })
  };

  componentDidUpdate(prevProps, prevState, snapshot) {
    if (this.props.id !== prevProps.id) {
      this.init(this.props.id)
    }
  }

  handleSave = () => {
    const data = {
      id: this.props.id,
      permIds: this.state.checked || []
    }
    this.setState({
      confirmLoading: true
    })
    HttpUtil.postForm('sysRole/grantPerm', data).then(rs => {
      this.setState({
        confirmLoading: false
      })
    })
  }




  onCheck = (ids) => {
    this.setState({checked: ids})
  }

  render() {
    let {treeData, confirmLoading, checked} = this.state
    if(treeData == null) {
      return "Loading..."
    }

    return <>
      <Tree
        treeData={treeData}
        multiple
        checkable
        checkedKeys={checked}
        onCheck={this.onCheck}
      >
      </Tree>
      <Divider/>
      <Button onClick={this.handleSave} loading={confirmLoading} type='primary'>保存权限</Button>
    </>
  }


}



