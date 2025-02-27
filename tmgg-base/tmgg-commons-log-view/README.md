# 日志监控

日志查看服务





# 访问

项目启动，访问

http://127.0.0.1:8080/log-view/index.html?path=D:/demo.log


## websocket接口

一个session对应一个监控线程，session断开线程关闭

请求格式

```
ws://127.0.0.1:8080/openApi/log?path=要实时监控的文件路径
```


## 使用
可参考demo.html，自行实现前端代码
```js
    webSocket = new WebSocket(target);
    webSocket.onopen = function () {
        console.log("已连接");
    };
    webSocket.onmessage = function (event) {
        console.log(event.data);
    };
    webSocket.onclose = function (e) {
        console.log("连接关闭", e);
    };

```

### 如果简单使用，可在页面中嵌入 iframe

例如 http://127.0.0.1:8080/log-view/index.html?path=D:/demo.log, 其中path需要encode


![img.png](doc/img.png)


### 如果使用React，可以使用 https://github.com/mozilla-frontend-infra/react-lazylog

```jsx
import React from 'react';
import { render } from 'react-dom';
import { LazyLog, ScrollFollow } from 'react-lazylog';

render((
  <ScrollFollow
    startFollowing={true}
    render={({ follow, onScroll }) => (
      <LazyLog url="http://127.0.0.1/openApi/log?path=D:\demo.log" stream follow={follow} onScroll={onScroll} />
    )}
  />
), document.getElementById('root'));

```


