import {defineConfig} from 'umi';



export const configLocal = {
    define: {
        "process.env.API_BASE_URL": "/api/"
    },

    proxy:{
        '/api': {
            target: 'http://127.0.0.1:8080',
            changeOrigin: true,
            pathRewrite: {'^/api': '/'},
        }
    }
};
export default defineConfig(configLocal);
