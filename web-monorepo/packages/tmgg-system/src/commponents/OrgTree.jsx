import {Alert, Skeleton, Tree} from 'antd';
import React from 'react';
import {http} from "@tmgg/tmgg-base";


export default class extends React.Component {

    state = {
        treeDataLoading: true,
        treeData: [],

        currentOrgId: null
    }


    componentDidMount() {
        http.get('sysOrg/tree').then(rs => {
            this.setState({treeData: rs})
            this.setState({treeDataLoading: false})
        })
    }

    onSelectOrg = orgIds => {
        if (orgIds.length > 0) {
            this.props.onChange(orgIds[0])
        } else {
            this.props.onChange(null)
        }
    }


    render() {
        let {treeData, treeDataLoading} = this.state
        if (treeDataLoading) {
            return <Skeleton title='加载中...'/>
        }

        if (treeData.length === 0) {
            return <Alert type={"warning"} message={'组织机构数据为空'}>
            </Alert>
        }

        return <Tree
            treeData={treeData}
            defaultExpandAll
            onSelect={this.onSelectOrg}
        >
        </Tree>
    }


}



