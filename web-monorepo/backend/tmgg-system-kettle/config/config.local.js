import {defineConfig} from 'umi';



export const configLocal = {
    define: {
        "process.env.API_BASE_URL": "/api/"
    },

    proxy:{
        '/api': {
            target: 'http://127.0.0.1:8002',
            changeOrigin: true,
            pathRewrite: {'^/api': '/'},
        }
    }
};
export default defineConfig(configLocal);
