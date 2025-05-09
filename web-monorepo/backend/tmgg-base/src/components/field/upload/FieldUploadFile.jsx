import { Button, Modal, Upload } from 'antd';

import React, {Component} from 'react';
import UploadOutlined from '@ant-design/icons/lib/icons/UploadOutlined';
import {SysUtil} from "../../../system";








/**
 * onChange?: (result: string) => void;
 *   value?: string;
 *   url?: string;
 *   businessKey?: string;
 *   accept?: string;
 *
 *   children:ReactNode;
 */
export class FieldUploadFile extends Component{
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
        if (f.status === 'done' && f.response && f.response.code == 0) {
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
    const defaultURL = SysUtil.getServerUrl() + 'sysFile/upload';
    let url = defaultURL;

    if (this.props.url != null) {
      url =  SysUtil.wrapServerUrl(this.props.url)  ;

    }

    if (businessKey != null) {
      url += '?businessKey=' + businessKey;
    }

    console.log('最终上传url', url)

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
          headers={SysUtil.getHeaders()}
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
    // TODO 显示
    return (
      <a >{this.props.children }</a>
    );
  }
}
