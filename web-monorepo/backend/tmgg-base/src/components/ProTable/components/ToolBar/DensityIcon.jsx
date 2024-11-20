import { ColumnHeightOutlined } from '@ant-design/icons';
import { Dropdown, Menu, Tooltip } from 'antd';
import React from 'react';


export class DensityIcon extends React.Component {

    render() {
        return (
            <Dropdown
                overlay={
                    <Menu
                        selectedKeys={[this.props.tableSize]}
                        onClick={({key}) => {
                            this.props.onTableSizeChange(key);
                        }}
                        style={{
                            width: 120,
                        }}
                        items={[
                            {key: 'large', label: '很大'},
                            {key: 'middle', label: '中等'},
                            {key: 'small', label: '紧凑'},
                        ]}
                    />
                }
                trigger={['click']}
            >
                <Tooltip title="表格密度">
                    <ColumnHeightOutlined/>
                </Tooltip>
            </Dropdown>
        );
    }
}


