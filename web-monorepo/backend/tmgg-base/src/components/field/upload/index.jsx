import React from 'react';
import { Button, Modal, Upload } from 'antd';
import UploadOutlined from '@ant-design/icons/lib/icons/UploadOutlined';
import {HttpUtil, SysUtil} from "../../../system";


let defaultUploadUrl = SysUtil.getServerUrl() + 'sysFile/upload';

/**
 * 多文件上传
 */
export class FieldMultipleUploadFile extends React.Component {
  handleChange = (e) => {

    const { fileList, file } = e;

    const ajaxResult = file.response;
    if (ajaxResult) {
      if (ajaxResult.success === false) {
        file.status = 'error';
        file.response = ajaxResult.message;
        Modal.error({
          title: '上传失败',
          content: ajaxResult.message,
        });
        return;
      }

      file.id = ajaxResult.data;
    }

    fileList.forEach((f) => {
      if (f.status === 'done' && f.response && f.response.code == 0) {
        f.id = f.response.data;
      }
    });

    this.props.onChange && this.props.onChange(fileList);
  };

  render() {
    let { url = defaultUploadUrl, value } = this.props;

    const fileList = [];
    if (value) {
      value.forEach((file) => {
        fileList.push({
          id: file.id,
          name: file.name || file.fileOriginName,
          status: 'done',
          fileName: file.name || file.fileOriginName,
        });
      });
    }

    return (
      <Upload
        action={url}
        defaultFileList={fileList}
        listType="text"
        onChange={this.handleChange}
        headers={SysUtil.getHeaders()}
      >
        <Button>
          <UploadOutlined /> 选择文件
        </Button>
      </Upload>
    );
  }
}
export * from './FieldUploadImage';
export * from './FieldUploadFile'
