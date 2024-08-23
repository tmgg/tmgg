import {Layout, Menu} from "antd";
import React from "react";
import {PageLoading} from "@ant-design/pro-components";

const {Sider, Content} = Layout;

export default class extends React.Component {

  state = {
    url: 'model.html',
  }

  render() {
    let {url} = this.state;


    let items = [
      {key: '0', label: '模型管理', url: 'model.html'},
      {key: '1', label: '我的任务', url: 'task.html'},
      {key: '3', label: '流程监控', url: 'monitor.html'}
    ];
    return <Layout style={{minHeight: 600}}>
      <Sider theme='light' width={100}>
        <Menu
          theme="light"
          items={items}
          onClick={(e) => {
            this.setState({
              url: e.item.props.url
            })
          }}
        />
      </Sider>
      <Content>
        {url ? <iframe sandbox="allow-scripts allow-same-origin allow-forms" src={url} width='100%' height='100%' frameBorder={0}
                       marginHeight={0}
                       marginWidth={0}></iframe> : <PageLoading/>}

      </Content>
    </Layout>

  }
}
