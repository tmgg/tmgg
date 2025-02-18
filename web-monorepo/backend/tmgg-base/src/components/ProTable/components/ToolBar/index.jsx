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
            showSearch,
            toolBarRender,

            tableSize,
            onTableSizeChange
        } = this.props;



        return <div className='pro-table-toolbar'>

            <div className='pro-table-toolbar-search'>
            {showSearch && <Input.Search
                style={{width: 200}}
                placeholder='搜索...'
                onSearch={(v)=>this.props.onSearch({keyword: v})}
            />
            }
            </div>

            <div className='pro-table-toolbar-option'>


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


