import {message, Spin, TreeSelect} from 'antd';

import React from 'react';
import {HttpClient} from '../../../system';
import {TreeUtil} from '../../../utils';
import {FieldProps} from "../FieldProps";

interface Props extends FieldProps {
  url: string
}

/**
 * 带checkbox的tree select
 * props : url
 */
export class FieldRemoteTreeCheckable extends React.Component<Props, any> {
  state = {
    data: [],
    fetching: false,
  };


  componentDidMount() {
    const {url} = this.props;
    this.fetchData(url);
  }


  fetchData = (url:string) => {
    this.setState({fetching: true});

    HttpClient.get(url).then((rs) => {
      if (rs == null || rs.success == false) {
        message.error(rs?.message || '异常');
        this.setState({fetching: false});
        return;
      }

      this.setState({data: rs.data, fetching: false});
    });
  };

  handleChange = (value:string[]) => {
    if (this.props.onChange) {
      this.props.onChange(value);
    }
  };

  render() {
    const {data} = this.state;
    let {value, mode, onChange, url, ...restProps} = this.props;

    if (this.state.fetching) {
      return <Spin/>;
    }

    if (mode == 'read') {
      return this.renderRead();
    }

    return (
      <TreeSelect
        {...restProps}
        style={{width: '100%'}}
        allowClear={true}
        dropdownStyle={{maxHeight: 400, overflow: 'auto'}}
        treeData={data}
        showCheckedStrategy={TreeSelect.SHOW_ALL}
        treeDefaultExpandAll={false}
        onChange={this.handleChange}
        multiple
        value={value || []}
        showArrow={true}
        treeCheckable
        treeLine
      />
    );
  }

  renderRead() {
    const {value} = this.props;
    if (value == null) {
      return '-';
    }

    const {data} = this.state;

    const itemList = TreeUtil.findByKeyList(data, value);

    return itemList.map((item:any) => item.title || item.label).join(',');
  }
}
