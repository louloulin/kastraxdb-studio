import React, { forwardRef, SVGProps } from 'react';
import classNames from 'classnames';
import * as LucideIcons from 'lucide-react';
import styles from './index.less';

export type IconName = keyof typeof LucideIcons;

export interface IconProps extends Omit<SVGProps<SVGSVGElement>, 'name'> {
  /**
   * 图标名称，来自 lucide-react
   */
  name: IconName;
  /**
   * 图标大小
   */
  size?: number | string;
  /**
   * 图标颜色
   */
  color?: string;
  /**
   * 描边宽度
   */
  strokeWidth?: number;
  /**
   * 自定义类名
   */
  className?: string;
  /**
   * 是否旋转
   */
  spin?: boolean;
  /**
   * 旋转速度
   */
  spinSpeed?: 'slow' | 'normal' | 'fast';
}

/**
 * 图标组件
 * 
 * 基于 lucide-react 实现，参考 Cursor 的图标实现
 */
const Icon = forwardRef<SVGSVGElement, IconProps>(
  (
    {
      name,
      size = 24,
      color,
      strokeWidth = 2,
      className,
      spin = false,
      spinSpeed = 'normal',
      ...rest
    },
    ref
  ) => {
    const LucideIcon = LucideIcons[name];

    if (!LucideIcon) {
      console.warn(`Icon "${name}" not found in lucide-react`);
      return null;
    }

    return (
      <LucideIcon
        ref={ref}
        size={size}
        color={color}
        strokeWidth={strokeWidth}
        className={classNames(
          styles.icon,
          {
            [styles.spin]: spin,
            [styles[`spin-${spinSpeed}`]]: spin && spinSpeed,
          },
          className
        )}
        {...rest}
      />
    );
  }
);

Icon.displayName = 'Icon';

export default Icon;
