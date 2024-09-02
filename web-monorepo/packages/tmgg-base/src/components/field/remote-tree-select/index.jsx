import { message, Spin, TreeSelect } from 'antd';

import React from 'react';
import {StrUtil, TreeUtil} from '../../../utils';
import {http} from "../../../system";


export class FieldRemoteTreeSelect extends React.Component {
  state = {
    data: [],
    value: [],
    fetching: false,
    key: this.props.id,
  };

  componentDidMount() {
    // 下载默认值的label
    this.fetchData();
  }

  componentWillReceiveProps(nextProps) {
    if (nextProps.url !== this.props.url) {
      this.setState({ url: nextProps.url }, this.fetchData);
    }
  }

  fetchData = () => {
    const { url } = this.props;
    this.setState({ fetching: true });

    http.get(url).then((rs) => {
      if (rs == null) {
        console.error(url, '未查询到数据');
        return;
      }
      if (rs.success === false) {
        message.error(rs.message);
        this.setState({ fetching: false });
        return;
      }

      this.setState({ data:  rs.data, fetching: false });
    });
  };

  handleChange = (value) => {
    console.log(value);
    if (this.props.onChange) {
      this.props.onChange(value);
    }
  };

  render() {
    const { props } = this;
    let { mode, value } = props;
    let { data } = this.state;

    if (this.state.fetching) {
      return <Spin />;
    }

    if (mode === 'read') {
      if (value == null) {
        return '';
      }

      const target = TreeUtil.findByKey(data, value);
      if (target) {
        return target.title;
      }
      return '';
    }


    return (
      <TreeSelect
        style={{ width: '100%', minWidth: 200 }}
        allowClear={true}
        dropdownStyle={{ maxHeight: 400, overflow: 'auto' }}
        treeData={data}
        showCheckedStrategy={TreeSelect.SHOW_ALL}
        treeDefaultExpandAll={false}
        onChange={this.handleChange}

        filterTreeNode={(inputValue, treeNode)=>{
          const {title} = treeNode
          return StrUtil.contains(title,inputValue)
        }}
        treeLine={{showLeafIcon:true}}

        {...this.props}
        value={value || undefined}
        showArrow={true}
      />
    );
  }
}
