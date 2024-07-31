# FieldRadioBoolean

```jsx
import React from 'react';
import { FieldRadioBoolean } from 'crec-web';
import { Button, Form, message, Modal } from 'antd';

export default class Test extends React.Component {
  render() {
    return (
      <Form
        onFinish={(values) => window.alert(JSON.stringify(values))}
        initialValues={{ a: true, b: false, c: null }}
      >
        <Form.Item name="a" label="a">
          <FieldRadioBoolean></FieldRadioBoolean>
        </Form.Item>
        <Form.Item name="b" label="b">
          <FieldRadioBoolean></FieldRadioBoolean>
        </Form.Item>
        <Form.Item name="c" label="c">
          <FieldRadioBoolean></FieldRadioBoolean>
        </Form.Item>

        <Button htmlType={'submit'}>查看结果</Button>
      </Form>
    );
  }
}
```

<API src="index.tsx"></API>
