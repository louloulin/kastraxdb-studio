import { theme } from 'antd';
import { PrimaryColorType } from '@/constants';
import { commonToken } from '../common';

type IAntdPrimaryColor = {
  [key in PrimaryColorType]: any;
};

// 主题色
const antdPrimaryColor: IAntdPrimaryColor = {
  [PrimaryColorType.Polar_Green]: {
    colorPrimary: '#3ecf8e', // Supabase 绿色，保持与暗色主题一致
  },
  [PrimaryColorType.Golden_Purple]: {
    colorPrimary: '#7c3aed', // 更深的紫色，亮色主题下更易辨识
  },
  [PrimaryColorType.Polar_Blue]: {
    colorPrimary: '#3b82f6', // 更现代的蓝色
  },
  [PrimaryColorType.Silver]: {
    colorPrimary: '#6b7280', // 更深的银色，亮色主题下更易辨识
  },
  [PrimaryColorType.Red]: {
    colorPrimary: '#ef4444', // 更现代的红色
  },
  [PrimaryColorType.Orange]: {
    colorPrimary: '#f59e0b', // 更现代的橙色
  },
  [PrimaryColorType.Blue2]: {
    colorPrimary: '#0ea5e9', // 更现代的蓝色
  },
  [PrimaryColorType.Gold]: {
    colorPrimary: '#b7791f', // 更现代的金色
  },
};

const antdLightTheme = {
  algorithm: [theme.defaultAlgorithm, theme.compactAlgorithm],
  customName: 'light',
  antdPrimaryColor,
  token: {
    ...commonToken,
    colorTextBase: '#1f2937', // 更深的文本颜色，提高对比度
    colorBgBase: '#ffffff', // 保持白色背景
    colorHoverBg: 'rgba(62, 207, 142, 0.05)', // 使用主色调的悬停效果
    colorBgContainer: '#ffffff', // 容器背景色
    colorBgSubtle: '#f8f9fa', // 更柔和的次要背景色
    colorBgElevated: '#ffffff', // 提升层级的背景色
    colorBorder: 'rgba(229, 231, 235, 0.8)', // 更清晰的边框颜色
    colorBorderSecondary: 'rgba(229, 231, 235, 0.5)', // 次要边框颜色

    // 添加额外的颜色变量，增强主题一致性
    colorPrimaryBg: 'rgba(62, 207, 142, 0.1)', // 主色背景，用于高亮区域
    colorPrimaryBgHover: 'rgba(62, 207, 142, 0.15)', // 主色背景悬停
    colorPrimaryBorder: 'rgba(62, 207, 142, 0.2)', // 主色边框
    colorPrimaryText: '#3ecf8e', // 主色文本

    // 改进错误、警告和成功状态的颜色
    colorError: '#ef4444', // 错误色
    colorWarning: '#f59e0b', // 警告色
    colorSuccess: '#10b981', // 成功色

    // 改进文本颜色层次
    colorTextSecondary: '#4b5563', // 次要文本颜色
    colorTextTertiary: '#9ca3af', // 第三级文本颜色
    colorTextQuaternary: '#d1d5db', // 第四级文本颜色
  },
};

export default antdLightTheme;
