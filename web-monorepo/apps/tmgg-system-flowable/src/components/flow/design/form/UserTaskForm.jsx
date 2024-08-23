import React from 'react';
import {Card, Form, Select, Switch} from 'antd';
import BpmnUtils from '../../BpmnUtils';
import {HttpClient} from "@crec/lang";

const PREFIX = 'flowable:';
/**
 * onFinish
 */
export default class extends React.Component {
  state = {
    assignmentTypeList: [],
    assignmentObjectList: [],

    assignmentTypeMap: {},

    initData: {},

    loading: true,
  };

  formRef = React.createRef();

  destroyed = false;

  constructor(props) {
    super(props);

    const data = {};
    data.assignmentType = this.props.bo.get(PREFIX + 'assignmentType');
    data.assignmentObject = this.props.bo.get(PREFIX + 'assignmentObject');
    data.genNodeLabel = this.props.bo.get(PREFIX + 'genNodeLabel'); // 自动生成节点名称

    // 默认自动生成
    if(data.genNodeLabel == null){
      data.genNodeLabel = true
      this.props.bo.set(PREFIX + 'genNodeLabel',true)
    }


    this.state.initData = data;
  }

  componentDidMount() {
    const {initData} = this.state;
    HttpClient.get('/flowable/model/assignmentTypeList').then((rs) => {
      if (this.destroyed) {
        return;
      }

      const assignmentTypeList = rs.data;
      const assignmentTypeMap = {};

      assignmentTypeList.forEach((a) => {
        assignmentTypeMap[a.code] = a;
      });

      this.setState({assignmentTypeList, assignmentTypeMap});

      // 如果已指定分配类型
      if (initData.assignmentType) {
        const assignmentTypeInfo = assignmentTypeMap[initData.assignmentType];


        this.setState({assignmentTypeInfo});
        this.loadAssignmentObjectTree(initData.assignmentType);

        // 如果已指定分配对象
        if (initData.assignmentObject != null && assignmentTypeInfo != null) {
          if (assignmentTypeInfo.multiple && !Array.isArray(initData.assignmentObject)) {
            initData.assignmentObject = initData.assignmentObject.split(',');
          }
        }
      }
      this.setState({loading: false});
      console.log('initData', initData);
    });
  }

  loadAssignmentObjectTree = (assignmentTypeCode) => {
    const url = '/flowable/model/assignmentObjectTree?code=' + assignmentTypeCode;
    HttpClient.get(url).then((rs) => {
      this.setState({assignmentObjectList: rs.data});
    });
  };


  onValuesChange = (changed, values) => {
    for (let k in changed) {

      let v = changed[k];


      if (v == null) {
        BpmnUtils.removeProperty(this.props.bo, PREFIX + k);
      } else {
        this.props.bo.set(PREFIX + k, v);
      }

      // 选择用户或角色等具体
      switch (k) {
        case 'assignmentObject':
          this.onAssignmentObjectChange(k, v, values)
          break;
        case 'assignmentType':
          this.onAssignmentTypeChange(k, v, values)
          break;
        case  'genNodeLabel':
          this.setNodeLabel(values.assignmentObject);
          break;
      }


    }
  };

  onAssignmentObjectChange = (k, v, values) => {
    // 设置自定义属性
    const typeInfo = this.state.assignmentTypeMap[values.assignmentType];
    const xmlKey = typeInfo.xmlAttribute;
    this.props.bo.set(PREFIX + xmlKey, v);

    // 设置节点名称
    this.setNodeLabel(v);
  }

  setNodeLabel = v => {
    let label = this.getAssignmentObjectLabel(v);
    if (this.props.bo.get(PREFIX + 'genNodeLabel')) {
      this.props.bo.set('name', label);
      this.props.modeling.updateLabel(this.props.node, label);
    }
  };

  getAssignmentObjectLabel(value) {
    if (value == null) {
      return null;
    }
    const list = this.state.assignmentObjectList;

    let isArray = Array.isArray(value);
    if (!isArray) {
      return list.find(i => i.value == value)?.label
    }

    // 数组则逗号拼接
    const labelArr = []
    for (let v of value) {
      labelArr.push(list.find(item => item.value == v)?.label)
    }
    return labelArr.join(',')
  }

  onAssignmentTypeChange = (k, v, values) => {
    if (v != null) {
      this.loadAssignmentObjectTree(v);
    }

    const assignmentTypeInfo = this.state.assignmentTypeMap[v];
    if (assignmentTypeInfo.multiple) {
      this.formRef.current.setFieldsValue({assignmentObject: []});
    } else {
      this.formRef.current.setFieldsValue({assignmentObject: null});
    }

    BpmnUtils.removeProperty(this.props.bo, PREFIX + 'assignmentObject');
    BpmnUtils.removeProperty(this.props.bo, PREFIX + 'assignee');
    BpmnUtils.removeProperty(this.props.bo, PREFIX + 'candidateGroups');
    BpmnUtils.removeProperty(this.props.bo, PREFIX + 'candidateUsers');
  }


  componentWillUnmount() {
    this.destroyed = true;
  }

  render() {
    if (this.state.loading) {
      return <Card loading={true} bordered={false}></Card>;
    }

    const {assignmentTypeList, assignmentObjectList} = this.state;

    const assignmentType = this.formRef.current?.getFieldValue('assignmentType');

    const assignmentTypeInfo = this.state.assignmentTypeMap[assignmentType];

    const assignmentObjectOptions = assignmentObjectList.map((t) => ({
        key: t.value,
        label: t.label,
        value: t.value
      }
    ))

    return (
      <div>
        <Form
          ref={this.formRef}
          onValuesChange={this.onValuesChange}
          initialValues={this.state.initData}
          layout={'vertical'}
        >
          <Form.Item label="人员分配类型" name="assignmentType">
            <Select allowClear={true}>
              {assignmentTypeList.map((t) => (
                <Select.Option key={t.code} value={t.code}>
                  {t.label}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>

          {assignmentTypeInfo && (
            <Form.Item label="人员分配目标" name="assignmentObject">
              <Select
                mode={assignmentTypeInfo.multiple ? 'multiple' : false}
                allowClear={true}
                showSearch={true}
                filterOption={(input, option) => option.label.includes(input)}
                options={assignmentObjectOptions}
              >
              </Select>
            </Form.Item>
          )}

          <Form.Item label='自动生成节点名称' name='genNodeLabel' valuePropName='checked' >
            <Switch></Switch>
          </Form.Item>

        </Form>
      </div>
    );
  }
}
