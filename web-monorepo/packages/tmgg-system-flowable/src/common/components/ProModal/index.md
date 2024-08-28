# ProModal

```jsx
import React from 'react';
import { AsynButton } from 'crec-web';
import { ProModal } from 'crec-web';
import { Button } from 'antd';

export default class  extends React.Component {
  modelRef = React.createRef();

  render() {
    return (
      <div>
        <Button onClick={() => this.modelRef.current.show()}> click to show modal</Button>

        <ProModal title={'你好'} ref={this.modelRef}>
          欢迎
        </ProModal>
      </div>
    );
  }
}
```

<API src="index.tsx"></API>
