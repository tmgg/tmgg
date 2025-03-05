import {Button, Form} from 'antd';
import React from 'react';
import {SearchOutlined} from "@ant-design/icons";


export default class SearchForm extends React.Component {

    formRef = React.createRef()


    render = () => {
        const {loading} = this.props;


        return (
            <Form
                layout="inline"
                onFinish={(values) => this.props.onSearch(values)}
                ref={(instance) => {
                    this.formRef.current = instance
                    this.props.formRef(instance)
                }}
            >
                {this.props.searchFormItemsRender(this.formRef.current)}
                <Button type="primary" htmlType="submit" loading={loading} icon={<SearchOutlined/>}>
                    查询
                </Button>
            </Form>
        );
    };
}
