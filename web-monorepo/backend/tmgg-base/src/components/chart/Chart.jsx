import React from "react";
import * as echarts from 'echarts';
import {HttpUtil} from "../../system";

export class Chart extends React.Component {

    componentDidMount() {
        this.init();
    }

    componentDidUpdate(prevProps, prevState, snapshot) {
        this.init()
    }

    init() {
        const {code} = this.props
        HttpUtil.get("sysChart/getOption/" + code).then(rs => {
            if(this.myChart) {
                this.myChart.clear()
            }
            this.myChart = echarts.init(this.domRef.current);
            this.myChart.setOption(rs, true);
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
        let height = this.props.height || '100%';
        return <div ref={this.domRef}
                    style={{width: '100%', minHeight: 500, height: height}}></div>
    }

}
