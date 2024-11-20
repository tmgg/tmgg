/**
 * 工具栏
 */
import {ReloadOutlined} from '@ant-design/icons';
import {Input, Tooltip} from 'antd';
import React from 'react';
import './index.less';
import {DensityIcon} from "./DensityIcon";


export default class Toolbar extends React.Component {

    render = () => {
        const {
            onRefresh,
            headerTitle,
            searchInput,
            toolBarRender,

            tableSize,
            onTableSizeChange
        } = this.props;

        console.log('headerTitle',headerTitle)


        return <div className='pro-table-toolbar'>
            <div className='pro-table-toolbar-title'>{headerTitle }</div>
            <div className='pro-table-toolbar-option'>
                {searchInput && <Input.Search
                    style={{width: 200}}
                    placeholder='搜索...'
                    onSearch={(v)=>this.props.onSearch({keyword: v})}
                />
                }

                {toolBarRender}
                <span onClick={onRefresh}>
            <Tooltip title='刷新'><ReloadOutlined/></Tooltip>
          </span>

                <span >
                    <Tooltip title='表格密度'>
                        <DensityIcon tableSize={tableSize} onTableSizeChange={onTableSizeChange}/>
                    </Tooltip>
                 </span>
            </div>

        </div>
    };
}


