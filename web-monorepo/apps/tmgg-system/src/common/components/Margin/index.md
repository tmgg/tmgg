# Margin 间隔2


11

```jsx
import React from 'react';
import {MarginY} from 'crec-web';
import {Card} from "antd";

export default () => <>

  <Card title='小间距'>
    <div>
      xxxxxxxxxxxxxxxxxxxxx
    </div>

    <MarginY/>

    <div>
      xxxxxxxxxxxxxxxxxxxxx
    </div>
  </Card>

  <Card title='中间距'>
    <div>
      xxxxxxxxxxxxxxxxxxxxx
    </div>

    <MarginY size='md'/>

    <div>
      xxxxxxxxxxxxxxxxxxxxx
    </div>
  </Card>

  <Card title='大间距'>
    <div>
      xxxxxxxxxxxxxxxxxxxxx
    </div>

    <MarginY size='lg'/>

    <div>
      xxxxxxxxxxxxxxxxxxxxx
    </div>
  </Card>


</>;
```

<API src="index.tsx"></API>
