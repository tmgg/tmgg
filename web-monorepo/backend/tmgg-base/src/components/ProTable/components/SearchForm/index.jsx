import {Button, Form} from 'antd';
import React from 'react';


export default class SearchForm extends React.Component {

    formRef = React.createRef()


    onReset = (value) => {
        const {
            pagination,
            beforeSearchSubmit = (searchParams) => searchParams,
            action,
            onFormSearchSubmit,
            onReset,
        } = this.props;
        const pageInfo = pagination
            ? omitUndefined({
                current: pagination.current,
                pageSize: pagination.pageSize,
            })
            : {};

        const omitParams = omit(
            beforeSearchSubmit({...value, ...pageInfo}),
            Object.keys(pageInfo),
        );
        onFormSearchSubmit(omitParams);
        // back first page
        action.current?.setPageInfo?.({
            current: 1,
        });
        onReset?.();
    };

    render = () => {
        const {loading} = this.props;


        return (
            <div style={{paddingBottom: 16, borderBottom: '1px solid #e8e8e8'}}>
                <Form
                    layout="inline"
                    onFinish={(values) => this.props.onSearch(values)}
                    onReset={() => this.onReset({})}
                    ref={(instance) => {
                        this.formRef.current = instance
                        if (this.props.formRef) {
                            this.props.formRef.current = instance
                        }
                    }}
                >
                    {this.props.searchFormItemsRender(this.formRef.current)}
                    <Button type="primary" htmlType="submit" loading={loading}>
                        查询
                    </Button>
                </Form>
            </div>
        );
    };
}
