import {defaultTheme} from '@vuepress/theme-default'
import {defineUserConfig} from 'vuepress/cli'
import {viteBundler} from '@vuepress/bundler-vite'

export default defineUserConfig({
    lang: 'zh-CN',
    title: 'TMGG开源',
    description: '小型后台管理框架',


    theme: defaultTheme({
        navbar: ['/'],
      contributors:false,

        repo:'tmgg/tmgg',
        editLinkPattern:':repo/edit/master/docs/docs/:path',
        editLink:true,

        sidebar: [
            '/get-start',
            { text: "编码特性说明", link: "/gendoc.md"},

            {

                text: '扩展插件',
                children: [
                    '/plugins/kettle',
                    '/plugins/payment',
                    '/plugins/flowable',
                ]
            },
            {

                text: '前端手册',
                children: [
                    '/frontend/get-start',
                    '/frontend/components',
                    '/frontend/utils',
                    '/frontend/ui',
                ]
            },
            '/log-file',
            '/qa',
            '/code-tips',




        ],
        sidebarDepth:1
    }),
    base: '/tmgg/',

    bundler: viteBundler(),


})
