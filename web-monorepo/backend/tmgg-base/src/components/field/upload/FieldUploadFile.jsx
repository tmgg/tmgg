import React from "react";
import ImgCrop from "antd-img-crop";
import {Modal, Upload} from "antd";
import UploadOutlined from "@ant-design/icons/lib/icons/UploadOutlined";
import {SysUtil} from "../../../system";


export class FieldUploadFile extends React.Component {

  state = {
    // 传入的参数
    maxCount: 5,
    cropImage: false,
    accept:".jpg,.png",

    // 内部参数
    previewVisible: false,
    previewImage: '',
    fileList: [],

    value: null // 都好分隔的文件id
  };

  constructor(props) {
    super(props);
    this.state.cropImage = this.props.cropImage
    this.state.maxCount = this.props.maxCount || 5;
    this.state.value = this.props.value
    this.state.fileList = this.convertValue2FileList(this.state.value);
    this.state.accept = this.props.accept
  }

  convertValue2FileList(value) {
    if (value && value.length > 0) {
      let list = [];

      for (const item of value) {
        const index = value.indexOf(item);
        let file = {};
        if (item.startsWith('http')) {
          file.url = item;
        } else {
          const id = item // f 相当于id了
          file.url = SysUtil.wrapServerUrl('sysFile/preview/' + id);
        }
        file.uid = index;
        file.name = 'image.png';
        file.status = 'done';
        file.fileName = item;
        list.push(file);
      }

    }

    return []
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


    this.setState({fileList});


    let fileIds = [];
    for (const f of fileList) {
      if (f.status === 'done' && f.response) {
        const ajaxResult = f.response
        if(ajaxResult.success){
          const {id,name} = ajaxResult.data
          fileIds.push(id);
        }else {
          Modal.error({title:'上传文件失败',content:ajaxResult.message})
        }

      }
    }
    if(fileIds.length>0){
      this.props?.onFileChange(fileList)
    }
    this.props?.onChange(fileIds.join(','));
  };


  render() {
    if(this.state.cropImage){
      return  <ImgCrop cropperProps={this.props.cropperProps} modalTitle={'裁剪图片'} fillColor={null}>
        {this.getUpload()}
      </ImgCrop>
    }

    return this.getUpload();
  }

  getUpload = () => {

    return <Upload
        action={SysUtil.wrapServerUrl('sysFile/upload')}
        listType="picture-card"
        fileList={this.state.fileList}
        onChange={this.handleChange}
        headers={SysUtil.getHeaders()}
        multiple={false}
        accept={this.state.accept}
        maxCount={this.state.maxCount}
    >
      {this.renderButton()}

    </Upload>;
  };

  renderButton = () => {
    const {fileList,maxCount} = this.state;
    if (fileList.length >= maxCount) {
      return
    }

    return <>
      <UploadOutlined/>
      <div className="ant-upload-text">选择文件</div>
    </>;
  };
}
