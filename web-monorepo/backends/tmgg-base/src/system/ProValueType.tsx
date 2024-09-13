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
