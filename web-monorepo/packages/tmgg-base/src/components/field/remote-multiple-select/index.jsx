import { message, Select, Spin } from 'antd';

import React from 'react';
import {http} from "../../../system";


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

  componentWillReceiveProps(nextProps, nextContext) {
    if (nextProps.url !== this.props.url) {
      this.setState({ url: nextProps.url }, () => {
        this.loadData();
      });
    }
  }

  handleSearch = (searchText) => {
    this.setState({ searchText, pageNumber: 1 });
    this.loadData(searchText);
  };

  loadData = (searchText = '') => {
    const { url } = this.state;
    this.setState({ fetching: true });

    httpUtil.get(url, { searchText }).then((rs) => {
      this.setState({ fetching: false });
      if (rs.success == false) {
        message.error(rs.message);
        return;
      }
      if (!(rs.data instanceof Array)) {
        message.error('返回结果的data字段应该为数组');
        return;
      }

      this.setState({ data: rs.data });
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

    console.log('默认值', value);

    // 默认为空数组
    if (value == undefined || value == null || value === '') {
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
