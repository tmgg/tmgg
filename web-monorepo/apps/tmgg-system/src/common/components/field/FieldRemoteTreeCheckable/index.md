# FieldRemoteTreeCheck 选择框

```jsx
import React from 'react';
import { FieldRemoteTreeCheckable } from 'crec-web';

export default class  extends React.Component {

    state = {
        value: null,
    }
    
    
  render() {
    return <>
      <FieldRemoteTreeCheckable url='sysOrg/tree' value={this.state.value}  onChange={v=>this.setState({value:v})}></FieldRemoteTreeCheckable>

      &nbsp;&nbsp;&nbsp;选中的值为：{JSON.stringify(this.state.value)}
    </>;
  }
}
```

<API src="index.tsx"></API>
