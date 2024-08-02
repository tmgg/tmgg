import { defineConfig } from 'vitepress'
import AutoSidebarPlugin from 'vitepress-auto-sidebar-plugin'


// https://vitepress.dev/reference/site-config
export default defineConfig({
  title: "TMGG DOCS",
  description: "TMGG开发文档",
  themeConfig: {
    // https://vitepress.dev/reference/default-theme-config
    nav: [
      { text: 'Home', link: '/' },
      { text: '文档', link: '/doc' },
      { text: '示例', link: '/markdown-examples' },
      { text: '升级', link: '/upgrade' }
    ],



    socialLinks: [
      { icon: 'github', link: 'https://github.com/tmgg/tmgg' }
    ]
  },
  vite: {
    plugins: [
      AutoSidebarPlugin({

      }),
    ],
  },
})
