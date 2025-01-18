import React from "react";
import {HttpUtil} from "../../system";
import {Alert, Spin, Table} from "antd";

export class SysReportTable extends React.Component {

    state = {
        loading: true,
        data: null
    }

    componentDidMount() {
        this.init();
    }

    componentDidUpdate(prevProps, prevState, snapshot) {
        if (prevProps.code !== this.props.code) {
            this.init()
        }

    }

    init = () => {
        const {code} = this.props
        this.setState({loading: true})
        HttpUtil.get("sysReportTable/getData/" + code).then(rs => {
            this.setState({data: rs})
        }).finally(() => {
            this.setState({loading: false}, this.renderChart)
        })
    };


    componentWillUnmount() {
        this.clean();
    }

    clean() {
        if (this.myChart) {
            this.myChart.dispose();
        }
    }

    render() {
        if (this.state.loading) {
            return <Spin size='large' tip='数据加载中...'>
                <Alert message='数据量大的情况可能加载数分钟，请等待!'></Alert>
            </Spin>
        }
        return <Table
            dataSource={this.state.data.dataList}
            size={"small"}
            bordered
            columns={this.state.data.keys.map(k => {
                return {
                    title: k,
                    dataIndex: k
                }
            })}></Table>
    }

}
