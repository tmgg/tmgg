import React from 'react';
import { Dropdown, Menu, Space } from 'antd';
import { DownSquareTwoTone } from '@ant-design/icons';
import {hasPermission} from "../../system";




interface Props{

  /**
   * 显示子节点的个数， 超过的为收缩起来
   */
  maxNum?:number;


  children: any[];

}




export class ButtonList extends React.Component<Props, any> {
  static defaultProps = {
    maxNum: 2
  }

  render() {
    let {children, maxNum} = this.props;


    const showList = [];
    const dropdownList = [];

    if (children.length > 0) {
      const menus = [];
      // 权限过滤
      for (let c of children) {
        if (c === null || c === undefined) {
          continue;
        }
        // @ts-ignore
        if ((c != false && hasPermission(c.props.perm)) || c.props == null || c.props.perm == null) {
          menus.push(c);
        }
      }

      // 添加分割线
      const len = menus.length;
      for (let i = 0; i < len; i++) {
        // @ts-ignore
        if (i + 1 <= maxNum) {
          showList.push(menus[i]);
        } else {
          dropdownList.push(menus[i]);
        }
      }
    } else {
      // 单节点
      const child = children;

      // @ts-ignore
      const childPerm = child.props == null ? null : child.props.perm;

      if (childPerm == null || hasPermission(childPerm)) {
        showList.push(child);
      }
    }

    return (
      <Space>
        {' '}
        {showList}
        {dropdownList.length > 0 && (
          <Dropdown
            overlay={
              <Menu>
                {dropdownList.map((d, i) => {
                  // @ts-ignore
                  let disabled = d.props.disabled;
                  return (
                    <Menu.Item key={i} disabled={disabled}>
                      {d}
                    </Menu.Item>
                  );
                })}
              </Menu>
            }
          >
            <a className="ant-dropdown-link" onClick={(e) => e.preventDefault()}>
              <DownSquareTwoTone style={{fontSize: 14}}/>
            </a>
          </Dropdown>
        )}
      </Space>
    );
  }
}