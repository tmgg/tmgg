import React from 'react';
import { Modal } from 'antd';
import {SysUtil} from "../../system";



export class ViewImage extends React.Component {
  preview = (url) => {
    Modal.info({
      title: '预览图片',
      width: '70vw',
      content: <div style={{height:'70vh',overflow:'auto'}}><img src={url} width="100%" /></div>,
    });
  };

  render() {
    let vs = this.props.value

    if (!vs) {
      return;
    }

    if (typeof vs === 'string') {
      vs = vs.split(',');
    }

    let urlList = [];
    for (let v of vs) {
      let isId = v.indexOf('/') === -1;
      let isAbsUrl = v.startsWith('http');
      if (isAbsUrl) {
        urlList.push(v);
        continue;
      }

      if (isId) {
        urlList.push(SysUtil.getServerUrl() + 'sysFile/preview/' + v);
        continue;
      }

      urlList.push(SysUtil.getServerUrl() + v);
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
