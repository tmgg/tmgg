# HasPerm 权限判断

```jsx
import React from 'react';
import {HasPerm,SysConfig} from 'crec-web';

export default () => {
    
    // 全局设置一次
  SysConfig.setPermissions(['user:add','user:view'])

  return <>
    <HasPerm code="user:add">
      <a> 添加 </a>
    </HasPerm>
    <HasPerm code="user:delete">
      <a> 删除 </a>
    </HasPerm>
    <HasPerm code="user:view" >
      <a> 查看 </a>
    </HasPerm>
  </>;
};
```

<API src="index.tsx"></API>
