import React from "react";
import {LeftRightLayout} from "../../../common/components/LeftRightLayout";
import Dict from "./Dict";
import DictItem from "./DictItem";

export default class extends React.Component {

    render() {
        return <LeftRightLayout leftSize='50%'>
            <Dict />
            <DictItem />

        </LeftRightLayout>
    }
}