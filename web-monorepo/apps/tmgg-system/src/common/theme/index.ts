import vars from './vars.less';

// 导出 样式相关变量，方便js使用
// 注意：导出为最终样式
const theme = {
  // 导出 antd
  primaryColor: vars['primary-color'],
  backgroundColorLight: vars['background-color-light'],
  backgroundColorBase: vars['background-color-base'],
  fontSizeBase: vars['font-size-base'],

  paddingLg: vars['padding-lg'],
  paddingMd: vars['padding-md'],
  paddingSm: vars['padding-sm'],
  paddingXs: vars['padding-xs'],
  paddingXss: vars['padding-xss'],

  marginLg: vars['margin-lg'],
  marginMd: vars['margin-md'],
  marginSm: vars['margin-sm'],
  marginXs: vars['margin-xs'],
  marginXss: vars['margin-xss'],

  // 自定义
  fieldWidthXs: vars['field-width-xs'],
  fieldWidthPadding: vars['field-width-padding'],

  fieldWidthSm: vars['field-width-sm'],
  fieldWidthMd: vars['field-width-md'],
  fieldWidthLg: vars['field-width-lg'],
  fieldWidthXl: vars['field-width-xl'],
};

console.log('风格变量', theme);

export { theme };
