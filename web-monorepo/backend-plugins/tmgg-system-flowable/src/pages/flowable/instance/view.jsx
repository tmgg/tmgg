import React from "react";
import InstanceInfo from "../../../components/InstanceInfo";
import {PageUtil} from "@tmgg/tmgg-base";

export default class extends React.Component {


    render() {
        const {businessKey, id} = PageUtil.currentParams()
        return <InstanceInfo businessKey={businessKey} id={id}/>
    }

}
