import antdDarkTheme from './background/dark';
import antdDarkDimmedTheme from './background/darkDimmed';
import antdLightTheme from './background/light';
import { ThemeType, PrimaryColorType } from '@/constants';
import { ITheme } from '@/typings/theme';
import lodash from 'lodash';
import { theme } from 'antd';

const antdThemeConfigs = {
  [ThemeType.Dark]: antdDarkTheme,
  [ThemeType.Light]: antdLightTheme,
  [ThemeType.DarkDimmed]: antdDarkDimmedTheme,
};

export function getAntdThemeConfig(_theme: ITheme) {
  const antdThemeConfig = lodash.cloneDeep(antdThemeConfigs[_theme.backgroundColor]);
  antdThemeConfig.token = {
    ...antdThemeConfig.token,
    ...(antdThemeConfig.antdPrimaryColor[_theme.primaryColor as PrimaryColorType] || {}),
  };

  const token = theme.getDesignToken(antdThemeConfig);
  injectThemeVar(token as any, _theme.backgroundColor, _theme.primaryColor);
  return antdThemeConfig;
}

// TODO: 只插入一次
export function injectThemeVar(token: { [key in string]: string }, _theme: ThemeType, primaryColor: PrimaryColorType) {
  let css = '';

  // 处理所有token变量
  Object.keys(token).map((t) => {
    const attributeName = camelToDash(t);
    let value = token[t];

    // 将需要px的数字带上px
    const joinPxArr = [
      'fontSize', 'fontSizeSM', 'fontSizeLG', 'fontSizeXL',
      'borderRadius', 'borderRadiusSM', 'borderRadiusLG', 'borderRadiusXL',
      'marginXS', 'marginSM', 'margin', 'marginMD', 'marginLG', 'marginXL',
      'paddingXS', 'paddingSM', 'padding', 'paddingMD', 'paddingLG', 'paddingXL'
    ];

    if (joinPxArr.includes(t) && typeof value === 'number') {
      value = value + 'px';
    }

    css = css + `--${attributeName}: ${value};\n`;
  });

  // 添加CSS变量过渡效果，使主题切换更平滑
  const transitionCSS = `
    transition:
      background-color 0.3s ease,
      color 0.3s ease,
      border-color 0.3s ease,
      box-shadow 0.3s ease;
  `;

  // 创建主题容器
  const container = `html[theme='${_theme}'],html[primary-color='${primaryColor}']{
    ${css}
  }

  /* 添加全局过渡效果 */
  html {
    ${transitionCSS}
  }

  body,
  .ant-layout,
  .ant-layout-header,
  .ant-layout-footer,
  .ant-layout-sider,
  .ant-layout-content,
  .ant-btn,
  .ant-input,
  .ant-select,
  .ant-dropdown-menu,
  .ant-menu,
  .ant-table,
  .ant-modal-content,
  .ant-drawer-content {
    ${transitionCSS}
  }`;

  // 查找是否已存在相同主题的样式标签
  const existingStyle = document.querySelector(`style[data-theme="${_theme}-${primaryColor}"]`);

  if (existingStyle) {
    // 如果存在，则更新内容
    existingStyle.textContent = container;
  } else {
    // 如果不存在，则创建新标签
    const style = document.createElement('style');
    style.type = 'text/css';
    style.setAttribute('data-theme', `${_theme}-${primaryColor}`);
    style.appendChild(document.createTextNode(container));
    document.head.appendChild(style);
  }

  // 保存主题配置到全局变量
  window._AppThemePack = token;
}

function camelToDash(str: string) {
  return str.replace(/([A-Z])/g, '-$1').toLowerCase();
}
