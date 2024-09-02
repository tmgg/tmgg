import React from "react";
import Dict from "./Dict";
import DictItem from "./DictItem";
import {Card, Col, Empty, Row} from "antd";

export default class extends React.Component {

    state = {
        selectedKey: null
    }


    render() {
        return <Row gutter={12}>
            <Col> <Dict onChange={selectedKey => this.setState({selectedKey})}/></Col>
            <Col>
                <DictItem sysDictId={this.state.selectedKey}/>
            </Col>
        </Row>


    }
}
