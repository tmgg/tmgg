import { Button, Modal, Upload } from 'antd';

import React, {Component, ReactNode} from 'react';
import UploadOutlined from '@ant-design/icons/lib/icons/UploadOutlined';
import {HttpClient, SysConfig} from "../../../system";
import {LinkToViewFile} from "../../LinkToViewFile";



/**
 *  changelog: jiangtao 调整为默认上传一个, 最新上传的有效
 * 文件上传组件
 * 可用属性：
 * url，不传则默认到文件中心
 * label: 按钮显示的文字
 */
const defaultURL = SysConfig.getServerUrl() + 'sysFile/upload';

interface UploadFileProps {
  onChange?: (result: string) => void;
  value?: string;
  url?: string;
  businessKey?: string;
  accept?: string;

  children:ReactNode;
}


export { FieldUploadFile as UploadFile };

export class FieldUploadFile extends Component<UploadFileProps, any> {
  state = {
    fileList: [],
  };

  componentDidMount() {
    // 初始值
    if (this.props.value) {
      let list = [];
      let value = this.props.value;
      if (value instanceof Array) {
      } else {
        value = value.split(',');
      }
      value.forEach((f, index) => {
        let file = {};

        file.url = f;
        file.uid = index;
        file.name = f.split('/')[f.split('/').length - 1];
        file.status = 'done';
        file.fileName = f;
        list.push(file);
      });
      this.setState({ fileList: list });
    }
  }

  handleChange = ({ fileList, event, file }) => {
    const rs = file.response;
    if (rs != null && rs.success === false) {
      Modal.error({
        title: '上传失败',
        content: rs.message,
      });
      return;
    }

    this.setState({ fileList });
    if (this.props.onChange) {
      let doneList = [];
      fileList.forEach((f) => {
        if (f.status === 'done' && f.response && f.response.code == 200) {
          doneList.push(f.response.data);
        }
      });
      if (doneList.length == fileList.length) {
        this.props.onChange(doneList.join(','));
      }
    }
  };

  render() {
    const { fileList } = this.state;
    const { businessKey, mode } = this.props;

    let url = defaultURL;

    if (this.props.url != null) {
      url = SysConfig.getServerUrl() + this.props.url;
    }

    if (businessKey != null) {
      url += '?businessKey=' + businessKey;
    }

    if (mode == 'read') {
      return this.renderReadOnly();
    }

    return (
      <div className="clearfix">
        <Upload
          maxCount={1}
          multiple={false}
          action={url}
          fileList={fileList}
          listType="text"
          onChange={this.handleChange}
          headers={HttpClient.getHeader()}
          accept={this.props.accept}
        >
          <Button>
            <UploadOutlined /> 选择文件
          </Button>
        </Upload>
      </div>
    );
  }

  renderReadOnly() {
    return (
      <LinkToViewFile value={this.props.value}>{this.props.children || '预览'}</LinkToViewFile>
    );
  }
}
