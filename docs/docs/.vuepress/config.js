import {defaultTheme} from '@vuepress/theme-default'
import {defineUserConfig} from 'vuepress/cli'
import {viteBundler} from '@vuepress/bundler-vite'
import { searchPlugin } from '@vuepress/plugin-search'

export default defineUserConfig({
    lang: 'zh-CN',

    title: 'TMGG开源',
    description: '安全稳定，省时省心',

    plugins: [
        searchPlugin({
            // 配置项
        }),
    ] ,

    theme: defaultTheme({
        navbar: ['/'],
      contributors:false,

        repo:'tmgg/tmgg',
        editLinkPattern:':repo/edit/master/docs/docs/:path',
        editLink:true,

        sidebar: [
            '/function',
            '/get-start',
            '/config',
            '/session',
            '/job',
            '/open-api',
            '/flowable',
            '/kettle',
            '/payment',
            {

                text: '前端手册',
                children: [
                    '/frontend/get-start',
                    '/frontend/components',
                    '/frontend/utils',
                ]
            },

            {

                text: '其他',
                children: [
                    '/code-tips',
                    '/qa'
                ]
            },



        ],
        sidebarDepth:1
    }),
    base: '/tmgg/',

    bundler: viteBundler(),


})
