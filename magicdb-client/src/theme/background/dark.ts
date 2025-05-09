import { theme } from 'antd';
import { PrimaryColorType } from '@/constants';
import { commonToken } from '../common';

type IAntdPrimaryColor = {
  [key in PrimaryColorType]: any;
};

// 主题色
const antdPrimaryColor: IAntdPrimaryColor = {
  [PrimaryColorType.Polar_Green]: {
    colorPrimary: '#3ecf8e', // Supabase 绿色
  },
  [PrimaryColorType.Golden_Purple]: {
    colorPrimary: '#8d7aee', // 更现代的紫色
  },
  [PrimaryColorType.Polar_Blue]: {
    colorPrimary: '#4cc9f0', // 更现代的蓝色
  },
  [PrimaryColorType.Silver]: {
    colorPrimary: '#9ca3af', // 更现代的银色
  },
  [PrimaryColorType.Red]: {
    colorPrimary: '#f87171', // 更现代的红色
  },
  [PrimaryColorType.Orange]: {
    colorPrimary: '#fbbf24', // 更现代的橙色
  },
  [PrimaryColorType.Blue2]: {
    colorPrimary: '#3b82f6', // 更现代的蓝色
  },
  [PrimaryColorType.Gold]: {
    colorPrimary: '#d4a45f', // 更现代的金色
  },
};

const antDarkTheme = {
  algorithm: [theme.darkAlgorithm, theme.compactAlgorithm],
  customName: 'dark',
  antdPrimaryColor,
  token: {
    ...commonToken,
    colorTextBase: '#f3f4f6', // 更亮的文本颜色，提高可读性
    colorBgBase: '#1f1f1f', // Supabase 风格的深色背景
    colorHoverBg: 'rgba(62, 207, 142, 0.08)', // 使用主色调的悬停效果
    colorBgContainer: '#1f1f1f', // 容器背景色
    colorBgSubtle: '#2a2a2a', // 次要背景色
    colorBgElevated: '#333333', // 提升层级的背景色
    colorBorder: 'rgba(75, 85, 99, 0.8)', // 更清晰的边框颜色
    colorBorderSecondary: 'rgba(75, 85, 99, 0.5)', // 次要边框颜色

    // 添加额外的颜色变量，增强主题一致性
    colorPrimaryBg: 'rgba(62, 207, 142, 0.1)', // 主色背景，用于高亮区域
    colorPrimaryBgHover: 'rgba(62, 207, 142, 0.15)', // 主色背景悬停
    colorPrimaryBorder: 'rgba(62, 207, 142, 0.2)', // 主色边框
    colorPrimaryText: '#3ecf8e', // 主色文本

    // 改进错误、警告和成功状态的颜色
    colorError: '#f87171', // 错误色
    colorWarning: '#fbbf24', // 警告色
    colorSuccess: '#34d399', // 成功色

    // 改进文本颜色层次
    colorTextSecondary: '#d1d5db', // 次要文本颜色
    colorTextTertiary: '#9ca3af', // 第三级文本颜色
    colorTextQuaternary: '#6b7280', // 第四级文本颜色
  },
};

export default antDarkTheme;
