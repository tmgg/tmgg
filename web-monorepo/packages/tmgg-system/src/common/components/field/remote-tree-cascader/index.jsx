import { Cascader, message, Spin } from 'antd';

import React from 'react';
import { TreeUtil } from '../../../utils';
import {HttpClient} from "../../../system";

/**
 * props : url
 */
export { RemoteTreeCascader as FieldRemoteTreeCascader };
export class RemoteTreeCascader extends React.Component {
  state = {
    data: [],
    value: [],
    fetching: false,
    key: this.props.id,
  };

  componentDidMount() {
    this.fetchData();
  }

  componentWillReceiveProps(nextProps) {
    if (nextProps.url !== this.props.url) {
      this.setState({ url: nextProps.url }, () => {
        this.fetchData();
      });
    }
  }

  fetchData = () => {
    const { url } = this.props;
    this.setState({ fetching: true });

    HttpClient.get(url).then((rs) => {
      if (rs == null) {
        console.error(url, '未查询到数据');
        return;
      }
      if (rs.success == false) {
        message.error(rs.message);
        this.setState({ fetching: false });
        return;
      }

      let list = rs.data;

      if (!(list instanceof Array)) {
        message.error('返回结果应该为数组');
        this.setState({ fetching: false });
        return;
      }

      this.setState({ data: list, fetching: false });
    });
  };

  handleChange = (arr) => {
    if (this.props.onChange) {
      this.props.onChange(arr[arr.length - 1]);
    }
  };

  render() {
    const { data, map, fetching } = this.state;
    if (fetching) {
      return <Spin />;
    }
    let { value, ...restProps } = this.props;

    let arr = [];
    if (value != null && value != undefined) {
      arr = TreeUtil.getKeyList(data, value);
    }

    return (
      <Cascader
        options={data}
        onChange={this.handleChange}
        value={arr}
        fieldNames={{ label: 'title', value: 'key' }}
        {...restProps}
      />
    );
  }
}
