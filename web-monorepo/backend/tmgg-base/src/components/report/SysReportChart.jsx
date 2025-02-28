import React from "react";
import * as echarts from 'echarts';
import {HttpUtil} from "../../system";
import {Alert, Skeleton, Spin} from "antd";

export class SysReportChart extends React.Component {

    state = {
        loading:true,
        data: null
    }

    componentDidMount() {
        this.init();
    }

    componentDidUpdate(prevProps, prevState, snapshot) {
        if(prevProps.code !== this.props.code){
            this.init()
        }

    }

    init = () => {
        const {code} = this.props
        if(code){
            this.setState({loading:true})
            HttpUtil.get("sysReportChart/getOption/" + code).then(rs => {
                this.setState({data:rs})
            }).finally(()=>{
                this.setState({loading:false},this.renderChart)
            })
        }
    };

    renderChart(){
        if(this.myChart) {
            this.myChart.clear()
        }
        this.myChart = echarts.init(this.domRef.current);
        this.myChart.setOption(this.state.data, true);
    }



    domRef = React.createRef()

    componentWillUnmount() {
        this.clean();
    }

    clean() {
        if (this.myChart) {
            this.myChart.dispose();
        }
    }

    render() {
        if(this.state.loading){
            return  <Spin size='large' tip='数据加载中...'>
                <Alert message='数据量大的情况可能加载数分钟，请等待!'></Alert>
            </Spin>
        }
        let height = this.props.height || '100%';
        return <div ref={this.domRef}
                    style={{width: '100%', minHeight: 500, height: height}}></div>
    }

}
