import { Modal, Upload } from 'antd';

import React from 'react';
import ImgCrop from 'antd-img-crop';
import { PlusOutlined } from '@ant-design/icons';
import {SysUtil} from "../../system";

const serverUrl = process.env.API_BASE_URL;
const fileUrl = serverUrl;

/**
 * crop: 带裁切
 */

export class FieldUploadImage extends React.Component {

  state = {
    previewVisible: false,
    previewImage: '',
    fileList: [],
  };

  componentDidMount() {
    // 初始值
    if (this.props.value && this.props.value.length > 0) {
      let list = [];
      let value = this.props.value;
      if (value instanceof Array) {
      } else {
        value = value.split(',');
      }
      value.forEach((f, index) => {
        let file = {};
        if (f.startsWith('http')) {
          file.url = f;
        } else {
          file.url = fileUrl + 'sysFile/preview?id=' + f;
        }
        file.uid = index;
        file.name = 'image.png';
        file.status = 'done';
        file.fileName = f;
        list.push(file);
      });
      this.setState({ fileList: list });
    }
  }

  handleCancel = () => this.setState({ previewVisible: false });

  handlePreview = async (file) => {
    if (!file.url && !file.preview) {
      file.preview = await getBase64(file.originFileObj);
    }

    this.setState({
      previewImage: file.url || file.preview,
      previewVisible: true,
    });
  };

  handleChange = (e) => {
    const { fileList } = e;
    this.setState({ fileList });
    if (this.props.onChange) {
      let fileIds = [];
      fileList.forEach((f) => {
        if (f.status === 'done' && f.response && f.response.code == 200) {
          fileIds.push(f.response.data);
        }
      });
      let files = fileIds.join(',');
      this.props.onChange(files);
    }
  };

  render() {
    const { previewVisible, previewImage, fileList } = this.state;
    const uploadButton = (
      <div>
        <PlusOutlined />
        <div className="ant-upload-text">添加</div>
      </div>
    );
    const maxNum = this.props.maxNum ? this.props.maxNum : 8;
    let multiple = this.props.multiple != null ? this.props.multiple : true;
    if (maxNum === 1) {
      multiple = false;
    }
    if (this.props.crop) {
      return (
        <div className="clearfix">
          <ImgCrop {...this.props}>
            <Upload
              action={serverUrl + 'common/upload'}
              listType="picture-card"
              fileList={fileList}
              onPreview={this.handlePreview}
              onChange={this.handleChange}
              headers={SysUtil.getHeaders()}
            >
              {fileList.length >= maxNum ? null : uploadButton}
            </Upload>
          </ImgCrop>
          <Modal open={previewVisible} footer={null} onCancel={this.handleCancel}>
            <img style={{ width: '100%' }} src={previewImage} />
          </Modal>
        </div>
      );
    }
    return (
      <div className="clearfix">
        <Upload
          action={serverUrl + 'sysFile/upload'}
          accept="image/*"
          listType="picture-card"
          fileList={fileList}
          onPreview={this.handlePreview}
          onChange={this.handleChange}
          multiple={multiple}
          headers={ SysUtil.getHeader() }
        >
          {fileList.length >= maxNum ? null : uploadButton}
        </Upload>
        <Modal open={previewVisible} footer={null} onCancel={this.handleCancel}>
          <img style={{ width: '100%' }} src={previewImage} />
        </Modal>
      </div>
    );
  }
}

function getBase64(file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => resolve(reader.result);
    reader.onerror = (error) => reject(error);
  });
}
