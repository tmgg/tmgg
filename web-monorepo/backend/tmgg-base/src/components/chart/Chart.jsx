import React from "react";
import * as echarts from 'echarts';
import {HttpUtil, PageUtil} from "../../system";
export class Chart extends React.Component {

    componentDidMount() {
        const {code} = this.props
        HttpUtil.get("sysChart/getOption/" + code).then(rs => {
            const myChart = echarts.init(this.domRef.current);
            myChart.setOption(rs, true);
            this.myChart = myChart;
        })
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
        return   <div ref={this.domRef}
                      style={{width: '100%', height: 500, marginTop: 36}}></div>
    }

}
