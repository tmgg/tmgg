// Umi 4 默认按页拆包，从而有更快的页面加载速度，由于加载过程是异步的，所以往往你需要编写 loading.tsx 来给项目添加加载样式，提升用户体验。


import React from "react";

export default class extends React.Component{

    render() {
        return <div>加载页面中....</div>
    }
}