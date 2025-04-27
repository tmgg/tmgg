import {Upload} from 'antd';

import React from 'react';
import ImgCrop from 'antd-img-crop';
import {PlusOutlined} from '@ant-design/icons';
import {SysUtil} from "../../../system";


export class FieldUploadCropImage extends React.Component {
    static defaultProps = {
        maxCount: 5
    }

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
            value.forEach((item, index) => {
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
            });
            this.setState({fileList: list});
        }
    }

    handleChange = (e) => {
        const {fileList} = e;
        this.setState({fileList});
        if (!this.props.onChange) {
            return;
        }
        let fileIds = [];
        fileList.forEach((f) => {
            if (f.status === 'done') {
                if (f.response && f.response.id) {
                    fileIds.push(f.response.id);
                }
            }
        });
        let files = fileIds.join(',');
        this.props.onChange(files);
    };


    render() {
        const {fileList} = this.state;
        return (
            <div className="clearfix">
                <ImgCrop cropperProps={this.props.cropperProps} modalTitle={'裁剪图片'} fillColor={null}>
                    <Upload
                        action={SysUtil.wrapServerUrl('sysFile/upload')}
                        listType="picture-card"
                        fileList={fileList}
                        onChange={this.handleChange}
                        headers={SysUtil.getHeaders()}
                        multiple={false}
                        accept={".jpg,.png"}
                        maxCount={this.props.maxCount}
                    >
                        {this.renderButton()}

                    </Upload>
                </ImgCrop>

            </div>
        );

    }

    renderButton = () => {
        const {fileList} = this.state;

        if(fileList.length >= this.props.maxCount){
            return
        }

        return <>
                <PlusOutlined/>
                <div className="ant-upload-text">添加</div>
        </>;
    };
}


