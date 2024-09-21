import { message, Select, Spin } from 'antd';

import React from 'react';
import {HttpUtil} from "../../system";


export class FieldRemoteMultipleSelect extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      url: props.url,
      data: [],
      fetching: false,
      searchText: null,
    };
  }

  componentDidMount() {
    this.loadData();
  }


  handleSearch = (searchText) => {
    this.setState({ searchText, page: 1 });
    this.loadData(searchText);
  };

  loadData = (searchText = '') => {
    const { url } = this.state;
    this.setState({ fetching: true });

    HttpUtil.get(url, { searchText }).then(rs => {
      this.setState({ fetching: false });
      if (!(rs instanceof Array)) {
        message.error('返回结果的data字段应该为数组');
        return;
      }

      this.setState({ data: rs });
    });
  };

  handleChange = (value) => {
    this.setState({ value, fetching: false });
    if (this.props.onChange) {
      this.props.onChange(value);
    }
  };

  render() {
    let { fetching, data, ...rest } = this.state;
    let { value } = this.props;


    // 默认为空数组
    if (value == null || value === '') {
      value = [];
    }

    return (
      <Select
        notFoundContent={fetching ? <Spin size="small" /> : null}
        filterOption={false}
        allowClear={true}
        onSearch={this.handleSearch}
        onChange={this.handleChange}
        disabled={this.props.disabled}
        mode="tags"
        value={value}
        style={this.props.style}
        {...rest}
      >
        {data.map((d) => (
          <Select.Option key={d.value}>{d.label}</Select.Option>
        ))}
      </Select>
    );
  }
}
