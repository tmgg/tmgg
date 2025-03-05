/**
 * 工具栏
 */
import {ReloadOutlined} from '@ant-design/icons';
import {Button, Input, Tooltip} from 'antd';
import React from 'react';
import './index.less';


export default class Toolbar extends React.Component {

    render = () => {
        const {
            onRefresh,
            showSearch,
            toolBarRender,
            loading,
            searchFormNode
        } = this.props;


        return <div className='pro-table-toolbar'>

            <div className='pro-table-toolbar-search'>
                {showSearch && <Input.Search
                    style={{width: 200}}
                    placeholder='搜索...'
                    onSearch={(v) => this.props.onSearch({keyword: v})}
                />
                }

                {searchFormNode}
            </div>

            <div className='pro-table-toolbar-option'>
                {toolBarRender}

                <Button icon={<ReloadOutlined/>} shape="circle" onClick={onRefresh} title='刷新' loading={loading}/>

            </div>

        </div>
    };
}


