import React from 'react';
import {Empty, Modal} from 'antd';
import {SysUtil} from "../../system";



export class ViewFile extends React.Component {


  render() {
    let fileId = this.props.value

    if (!fileId) {
      return <Empty />;
    }

     const  arr = fileId.split(',');

    let urlList = arr.map(id=> SysUtil.wrapServerUrl('sysFile/preview/' + id));


    const imgs = urlList.map((url) => (
      <iframe
        key={url}
        src={url}
        width='99%'
        frameBorder={0}
        height='99%'
      />
    ));

    return imgs;
  }
}
