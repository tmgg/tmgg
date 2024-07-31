# FieldSelectPosition 地图

通过 configAMapKey 全局设置高德地图的key

## 简单用法

```jsx
import React from 'react';

import {FieldSelectPosition} from 'crec-web';


export default class extends React.Component {
  state = {
    value: null
  }

  render() {
    return (
      <div>
        <FieldSelectPosition
          value={this.state.value}
          onChange={value => this.setState({value})}>
        </FieldSelectPosition>

        <div>
          已选择的经纬度：{this.state.value}
        </div>
      </div>)
  }
}
```

<API src="index.tsx"></API>
