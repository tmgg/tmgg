import React from 'react';
import {Empty, Skeleton} from 'antd';

import {getRouteByPath, SysConfig, tools} from '../../system';

/**
 * 像使用组件一样引入一个页面
 *
 * @param routePath
 */
interface RouterViewProps {
  path: string;

  // 弃用
  routePath?: string;

  iframe?: boolean;

  ref?: any;
  loading?: boolean;
}

export class RouterView extends React.Component<RouterViewProps, any> {

  constructor(props: RouterViewProps) {
    super(props);
    const {ref} = props;
    if (ref) {
      ref.current = this;
    }
  }


  elementRef: any = React.createRef();

  public getElement = () => {
    return this.elementRef.current;
  }


  render() {
    const {props} = this;
    console.log('route view', props);
    let {routePath, path, iframe = false} = props;
    // @ts-ignore
    path = path || routePath;

    if (path.startsWith('/#')) {
      path = path.replace('/#', '');
    }


    if (iframe) {
      path = SysConfig.appendTokenToUrl(path)
      console.log('iframe url', path)
      return (
        <iframe
          src={path}
          width="100%"
          height="100%"
          frameBorder={0}
          style={{minHeight: 500}}
          marginHeight={0}
          marginWidth={0}
        ></iframe>
      );
    }

    let queryIndex = path.indexOf('?');
    let query = {};
    if (queryIndex != -1) {
      query = tools.getQueryParams(path);
      path = path.substring(0, queryIndex);
    }

    const route = getRouteByPath(path);

    if (this.props.loading) {
      return <Skeleton/>;
    }

    if (route != null) {
        return  React.createElement(route.component, {location: {query}, ref:this.elementRef});
    }

    return (
      <div style={{minHeight: 600, padding: 100}}>
        <Empty description={'404,页面不见了！ ' + props.routePath}/>
      </div>
    )
  }


}
