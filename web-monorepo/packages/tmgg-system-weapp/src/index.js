import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import './index.css'
import ProviderTableContainer from "@ant-design/pro-table";

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    <React.StrictMode>
            <App/>
    </React.StrictMode>
);


// 非iframe时，增加padding，好看点
document.addEventListener('DOMContentLoaded', function () {
    if (window.location !== window.parent.location) {
        document.body.style.padding = '12px'
    }
});
