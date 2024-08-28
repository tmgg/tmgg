import {Input} from 'antd';
import React from 'react';
import {
  Editor, Ellipsis,
  FieldDictRadio,
  FieldDictSelect,
  FieldRadioBoolean, FieldRemoteMultipleSelect,
  FieldRemoteSelect,
  FieldRemoteTreeSelect,
  FieldSwitchYN,
  FieldUploadImage,
  RemoteImageView,
  FieldRemoteTreeMultipleSelect
} from './components';

/**
 * 扩展ValueType
 https://procomponents.ant.design/components/schema#valuetype
 **/
console.log('原始的valueType');
console.log('password  密码输入框\n money  金额输入框\n textarea  文本域\n date  日期\n dateTime  日期时间\n dateWeek  周\n dateMonth  月\n dateQuarter  季度输入\n dateYear  年份输入\n dateRange  日期区间\n dateTimeRange  日期时间区间\n time  时间\n timeRange  时间区间\n text  文本框\n select  下拉框\n checkbox  多选框\n rate  星级组件\n radio  单选框\n radioButton  按钮单选框\n progress  进度条\n percent  百分比组件\n digit  数字输入框\n second  秒格式化\n avatar  头像\n code  代码框\n switch  开关\n fromNow  相对于当前时间\n image  图片\n jsonCode  代码框，但是带了 json 格式化\n color  颜色选择器',);

const dictSelect = {
  render(text:any, props:any) {
    return (
      <FieldDictSelect {...props?.fieldProps} typeCode={props.params} value={text} mode="read"/>
    );
  },
  renderFormItem: (text:any, props:any) => {
    return <FieldDictSelect {...props?.fieldProps} typeCode={props.params} value={text}/>;
  },
};
const dictRadio = {
  render(text:string, props:any) {
    return (
      <FieldDictRadio {...props?.fieldProps} typeCode={props.params} value={text} mode="read"/>
    );
  },
  renderFormItem: (text:string, props:any) => {
    return <FieldDictRadio {...props?.fieldProps} typeCode={props.params} value={text}/>;
  },
};

const remoteTreeSelect = {
  render(text:string, props:any) {
    return text;
  },
  renderFormItem: (text:string, props:any) => {
    return (
      <FieldRemoteTreeSelect
        url={props.params}
        {...props?.fieldProps}
        value={text}
        treeDefaultExpandAll={true}
      />
    );
  },
};

const remoteSelect = {
  render(text:string, props:any) {
    return <FieldRemoteSelect url={props.params} {...props?.fieldProps} value={text} mode="read"/>;
  },
  renderFormItem: (text:string, props:any) => {
    return <FieldRemoteSelect url={props.params} {...props?.fieldProps} value={text}/>;
  },
};

const switchYN = {
  render(text:string, props:any) {
    return <FieldSwitchYN {...props?.fieldProps} value={text} mode="read"></FieldSwitchYN>;
  },
  renderFormItem: (text:string, props:any) => {
    return <FieldSwitchYN {...props?.fieldProps} value={text}></FieldSwitchYN>;
  },
};

const img = {
  render(text:string, props:any) {
    return <RemoteImageView fileId={text}/>;
  },
  renderFormItem: (text:string, props:any) => {
    return <FieldUploadImage {...props?.fieldProps} value={text}/>;
  },
};

export const LibValueType = {
  dictSelect,
  dict: dictSelect, // dict 别名，简写
  dictRadio,
  remoteTreeSelect,
  remoteSelect,
  switchYN,
  img,
  remoteMultipleSelect: {
    renderFormItem: (text:string, props:any) => {
      return (
        <FieldRemoteMultipleSelect
          url={props.params}
          {...props?.fieldProps}
          value={text}
        ></FieldRemoteMultipleSelect>
      );
    },
    render(text:string, props:any) {
      return text;
    },
  },

  remoteTreeMultipleSelect: {
    renderFormItem: (text:string, props:any) => {
      const url = props.params;
      return (
        <FieldRemoteTreeMultipleSelect
          url={url}
          {...props?.fieldProps}
          value={text}
        ></FieldRemoteTreeMultipleSelect>
      );
    },
    render(text:string, props:any) {
      return text;
    },
  },

  boolean: {
    render(value:any, props:any) {
      if (value === true) {
        return '是';
      }
      if (value === false) {
        return '否';
      }
    },
    renderFormItem: (text:any, props:any) => {
      return <FieldRadioBoolean {...props?.fieldProps}></FieldRadioBoolean>;
    },
  },

  link: {
    render: (text:string) => <a>{text}</a>,
    renderFormItem: (text:string, props:any) => {
      return <Input placeholder="请输入链接" {...props?.fieldProps} />;
    },
  },

  editor: {
    render(text:string, props:any) {
      return <Ellipsis>{text}</Ellipsis>;
    },
    renderFormItem: (text:string, props:any) => {
      return <Editor {...props?.fieldProps} value={text}/>;
    },
  },
};

console.log('框架扩展的valueType');
for (let k in LibValueType) {
  console.log(k);
}
