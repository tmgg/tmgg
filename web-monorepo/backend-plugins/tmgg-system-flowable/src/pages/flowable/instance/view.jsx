import React from "react";
import InstanceInfo from "../../../components/InstanceInfo";

export default class extends React.Component {


    render() {
        const {businessKey, id} = this.props.location.params
        return <InstanceInfo businessKey={businessKey} id={id}/>
    }

}
