import {defaultTheme} from '@vuepress/theme-default'
import {defineUserConfig} from 'vuepress/cli'
import {viteBundler} from '@vuepress/bundler-vite'

export default defineUserConfig({
    lang: 'zh-CN',

    title: '开发文档',
    description: '一个后台管理框架',

    theme: defaultTheme({
        navbar: ['/'],
        sidebar: [
            '/',
            '/data-init',
            '/session',
            '/job',
            '/kettle',

            '/code-tips',
            '/qa'
        ],
        sidebarDepth:1
    }),
    base: '/tmgg/',

    bundler: viteBundler(),


})
