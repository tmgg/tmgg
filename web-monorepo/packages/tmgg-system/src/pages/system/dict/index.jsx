import React from "react";
import {LeftRightLayout} from "../../../common/components/LeftRightLayout";
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
                <Card title='字典项列表'>
                {this.state.selectedKey == null ?
                    <Empty description='请选择字典'></Empty>
                    :                        <DictItem selectedKey={this.state.selectedKey}/>
                } </Card>

            </Col>
        </Row>


    }
}