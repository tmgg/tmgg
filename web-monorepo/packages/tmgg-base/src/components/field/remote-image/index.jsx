import React from 'react';
import { Card } from 'antd';
import {http} from "../../../system";

let fileURL = process.env.API_BASE_URL;

export { RemoteImage as FieldRemoteImage };
export class RemoteImage extends React.Component {
  state = {
    id: ('img' + Math.random()).replace('.', ''),
    loading: false,
  };

  componentDidMount() {
    this.setState({ loading: true });
    const { src } = this.props;
    const { id } = this.state;

    httpUtil.getBlob(fileURL + src).then((response) => {
      if (response) {
        response.blob().then((buffer) => {
          const urlCreator = window.URL || window.webkitURL;
          const imageUrl = urlCreator.createObjectURL(buffer);

          this.setState({ loading: false });

          const img = document.querySelector('#' + id);
          if (img) {
            setTimeout(() => {
              img.src = imageUrl;
            }, 100);
          }
        });
      }
    });
  }

  render() {
    const { width, height } = this.props;
    const { id, loading } = this.state;

    if (loading) {
      return <Card loading={true}>图片加载中</Card>;
    }

    return <img id={id} width={width} height={height} />;
  }
}
