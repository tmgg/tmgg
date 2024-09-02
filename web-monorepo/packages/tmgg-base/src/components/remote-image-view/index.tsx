import React from 'react';
import { Modal } from 'antd';
import {SysConfig} from "../../system";

interface IProps {
  // 推荐使用 ， 支持id， url， 并支持逗号分隔符
  value?: string | string[];

  // 弃用
  fileId?: string | string[];

  // 弃用
  url?: string | string[];
}

export interface IState {
  visible: boolean;
  previewUrl: string | null;
}

/**
 *  可以使用 fileId 或 url 属性
 */
export class RemoteImageView extends React.Component<IProps, IState> {
  preview = (url: string) => {
    Modal.info({
      title: '预览图片',
      width: '70vw',
      content: <img src={url} width="100%" />,
    });
  };

  render() {
    let vs = this.props.fileId || this.props.url || this.props.value;
    if (vs == null || vs == '' || vs.length == 0) {
      return;
    }
    if (typeof vs === 'string') {
      vs = vs.split(',');
    }

    let urlList = [];
    for (let v of vs) {
      let isId = v.indexOf('/') == -1;
      let isAbsUrl = v.startsWith('http');
      if (isAbsUrl) {
        urlList.push(v);
        continue;
      }

      if (isId) {
        urlList.push(SysConfig.getServerUrl() + 'sysFile/preview/' + v);
        continue;
      }

      urlList.push(SysConfig.getServerUrl() + v);
    }

    const imgs = urlList.map((url) => (
      <img
        style={{ display: 'inline-block' }}
        key={url}
        src={url}
        onClick={() => this.preview(url)}
        width={60}
        height={60}
      />
    ));

    return imgs;
  }
}
