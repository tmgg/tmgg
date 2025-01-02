import {defineConfig} from 'umi';


let host = 'localhost:8002';
let proxy = {
    '/api': {
        target: 'http://' + host,
        changeOrigin: true,
        pathRewrite: {'^/api': '/'},
    },
    '/ws-log-view': {
        target: 'http://' + host,
        changeOrigin: true,
        ws: true,
    },
    '/container/log': {
        target: 'http://' + host,
        changeOrigin: true,
        ws: true,
    },

};

export const configLocal = {


    define: {
        "process.env.API_BASE_URL": "/api/"
    },

    proxy
};
export default defineConfig(configLocal);
