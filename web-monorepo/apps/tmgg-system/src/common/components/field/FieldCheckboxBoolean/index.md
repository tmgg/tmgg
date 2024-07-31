# FieldCheckboxBoolean

```jsx
import React from 'react';
import {FieldCheckboxBoolean} from 'crec-web';
import {Button, Form, Modal} from 'antd';

export default class Test extends React.Component {
  
  render() {
    return (
        <Form onFinish={values => window.alert(JSON.stringify(values))}>
          <Form.Item name='isAdult' label='是否成年'>
            <FieldCheckboxBoolean></FieldCheckboxBoolean>
          </Form.Item>
          <Form.Item name='isOld' label='是否老人'>
            <FieldCheckboxBoolean></FieldCheckboxBoolean>
          </Form.Item>

          <Button htmlType='submit'>提交</Button>
        </Form>
    );
  }
}
```

## 阅读模式
mode='read'
```jsx
import React from 'react';
import { FieldCheckboxBoolean } from 'crec-web';
import { Form } from 'antd';

export default class Test extends React.Component {
  render() {
    return (
        <div>
          <Form initialValues={{isAdult:true, isOld: false}}>
            <Form.Item name='isAdult' label='是否成年'>
              <FieldCheckboxBoolean mode='read'></FieldCheckboxBoolean>
            </Form.Item>

            <Form.Item name='isOld' label='是否老人'>
              <FieldCheckboxBoolean mode='read'></FieldCheckboxBoolean>
            </Form.Item>
          </Form>
        </div>
    );
  }
}
```

<API src="index.tsx"></API>
