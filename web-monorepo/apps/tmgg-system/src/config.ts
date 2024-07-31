export const serverUrl = process.env.API_BASE_URL

/*
{
  "primary-color": "#1961AC",
  "success-color": "#52c41a",
  "warning-color": "#faad14",
  "error-color": "#ff4d4f"
}
*/
const umiDefTheme: any = process.env.theme
// 转换为驼峰
// @ts-ignore
export const theme = {
  primaryColor: umiDefTheme['primary-color'],
  successColor: umiDefTheme['success-color'],
  warningColor: umiDefTheme['warning-color'],
  errorColor: umiDefTheme['error-color']
}



console.log('一下为config.js中解析后的配置文件，代码中可使用--------')
console.log('服务器URL为', serverUrl)
console.log('主题设置', theme)
console.log('-------')

export default {
  theme,
  serverUrl,
}

// 高德地图key
export const mapKey = ""
