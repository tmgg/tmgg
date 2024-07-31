# Ellipsis 文本缩略

## 长文本变缩略号



```jsx
import React from 'react';
import { Ellipsis } from 'crec-web';

export default () => <>
  
  <div>
     <Ellipsis>短文本</Ellipsis>
  </div>
  
  <div>
    <Ellipsis>长文本长文本长文本长文本长文本长文本长文本</Ellipsis>
  </div>

  <div>
    <Ellipsis cutLength={100}>长文本长文本长文本长文本长文本长文本长文本</Ellipsis>
  </div>
</>;
```

<API src="index.tsx"></API>
