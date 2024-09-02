import React from 'react';
import {Form, Select} from 'antd';
import {http} from "@tmgg/tmgg-base";

export default class extends React.Component {
  bo = null;

  state = {
    data: {},

    selectOptions: [],

    javaDelegateOptions:[]
  }

  constructor(props) {
    super(props);
    this.bo = props.bo;

    const data = {};
    data['flowable:class'] = this.bo.get('flowable:class');

    this.state.data = data;
  }

  componentDidMount() {
    httpUtil.get('flowable/model/javaDelegateOptions').then(rs=>{
      this.setState({javaDelegateOptions:rs.data})

    })
  }

  onValuesChange = (changedValue) => {
    for (let key in changedValue) {
      this.bo.set(key, changedValue[key]);
    }
  };

  render() {
    return (
      <div>
        <Form
          onValuesChange={this.onValuesChange}
          initialValues={this.state.data}
          layout={'vertical'}
        >
          <Form.Item label="监听类" name="flowable:class" extra="需实现JavaDelegate接口">
            <Select options={this.state.javaDelegateOptions} ></Select>
          </Form.Item>
        </Form>
      </div>
    );
  }
}
