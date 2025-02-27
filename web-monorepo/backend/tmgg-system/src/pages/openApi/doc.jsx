import React from "react";
import {HttpUtil} from "@tmgg/tmgg-base";

export default class extends React.Component {

    state = {
        apiList: []
    }
    componentDidMount() {
        const id = this.props.params.id

        HttpUtil.get('openApiAccount/docInfo', {id}).then(rs=>{
            this.setState({apiList:rs})
        })
    }

    render() {
        const {apiList} =this.state
        return <div>接口文档

            {apiList.map(api=>{
                return <div>
                <h3>{api.name}</h3>
                    {JSON.stringify(api)}
                </div>
            })}


        </div>
    }
}
