import {Button, Card, Form, Space} from 'antd';
import React from 'react';
import './index.less';


export default class SearchForm extends React.Component {

    onSubmit = (value, firstLoad) => {
        const {
            pagination,
            action,
            onSubmit,
            onFormSearchSubmit,
        } = this.props;
        // 只传入 pagination 中的 current 和 pageSize 参数
        const pageInfo = pagination
            ? omitUndefined({
                current: pagination.current,
                pageSize: pagination.pageSize,
            })
            : {};

        const submitParams = {
            ...value,
            _timestamp: Date.now(),
            ...pageInfo,
        };
        const omitParams = omit(beforeSearchSubmit(submitParams), Object.keys(pageInfo)) ;
        onFormSearchSubmit(omitParams);
        if (!firstLoad) {
            // back first page
            action.current?.setPageInfo?.({
                current: 1,
            });
        }
        if (onSubmit && !firstLoad) {
            onSubmit?.(value);
        }
    };

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
        const {columns, loading, form} = this.props;
        if (columns == null) {
            return;
        }

        const columnsFilter = columns.filter(
            (col) => col.hideInSearch !== true && col.valueType !== 'option',
        );

        return (
            <Card style={{marginBottom: 16}}>
                <Form
                    className='pro-table-search-form'
                    layout="inline"
                    onFinish={(values) => this.onSubmit(values, false)}
                    onReset={() => this.onReset({})}
                    {...form}
                    ref={this.props.formRef}
                >
                    {columnsFilter.map((col, index) => {
                        const label = col.title ;

                        const Field = getField(col.valueType);

                        return (
                            <Form.Item label={label} name={col.dataIndex} key={index}>
                                <Field mode="edit" {...col.fieldProps}  />
                            </Form.Item>
                        );
                    })}

                    <Space>
                        <Button htmlType="reset">重置</Button>
                        <Button type="primary" htmlType="submit" loading={loading}>
                            查询
                        </Button>
                    </Space>
                </Form>
            </Card>
        );
    };
}
