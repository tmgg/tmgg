import { message, Select, Spin } from 'antd';

import React from 'react';
import {HttpUtil} from "../../system";

const { Option } = Select;


export class FieldRemoteSelect extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      url: props.url,
      data: [],
      value: [],
      loading: false,
      searchText: null,
    };
  }

  componentDidMount() {
    this.loadData({});
  }

  componentDidUpdate(prevProps, prevState, snapshot) {
    this.setState({ url: this.props.url }, () => {
      this.loadData({});
    });
  }



  handleSearch = (searchText) => {
    this.setState({ searchText });
    this.loadData({ searchText: searchText });
  };

  loadData = ({ searchText }) => {
    this.setState({ loading: true });

    const selected = this.props.value;
    const { url } = this.state;

    HttpUtil.get(url, { searchText, selected: selected }).then(data => {
      this.setState({ loading: false });


      if (!(data instanceof Array)) {
        message.error('返回结果的data字段应该为数组');
        return;
      }


      this.setState({ data });
      if (this.props.onLoadedData) {
        this.props.onLoadedData(data);
      }
    });
  };

  handleChange = (value) => {
    this.setState({ value, loading: false });
    if (this.props.onChange) {
      this.props.onChange(value);
    }
  };

  render() {
    const { loading, data } = this.state;

    let { value, mode } = this.props;

    // 判断值是否存在于列表中
    let valueExistInData = data.filter((d) => d.value == value).length;

    if (mode === 'read') {
      if (value == null) {
        return '';
      }
      if (valueExistInData) {
        return data.filter((d) => d.value == value)[0].label;
      }
      return value;
    }

    if (!valueExistInData) {
      value = null;
    }

    return (
      <Select
        allowClear
        showSearch
        style={{ width: '100%', minWidth: 200 }}
        notFoundContent={loading ? <Spin size="small" /> : null}
        filterOption={false}
        onSearch={this.handleSearch}
        onChange={this.handleChange}
        {...this.props}
        value={value}
      >
        {data.map((d) => (
          <Option key={d.value}>{d.label}</Option>
        ))}
      </Select>
    );
  }
}
