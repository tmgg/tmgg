/**
 * 组织机构树
 */
import React from "react";
import {FieldProps} from "../FieldProps";
import {Tree} from "antd";
import {http} from "../../../system";
import {LoadingOutlined} from "@ant-design/icons";

interface Props extends FieldProps {
}


export default class extends React.Component<Props, any> {

  state = {
    treeData: null
  }


  componentDidMount() {
    const params: any = {
    };

    http.get("/sysOrg/tree", params).then(rs => {
      console.log(rs.data)
      this.setState({treeData: rs.data})
    })
  }

  render() {
    if (this.state.treeData == null) {
      return <LoadingOutlined/>
    }

    return <Tree.DirectoryTree defaultSelectedKeys={[this.props.value]}
                               expandAction={false}
                               treeData={this.state.treeData}
                               defaultExpandAll
                               onSelect={this.onSelect}/>;
  }

  onSelect = (keys: any[]) => {
    this.props.onChange(keys[0])
  }

}