import React, { ButtonHTMLAttributes, ReactNode } from 'react';
import classNames from 'classnames';
import { Spin } from 'antd';
import styles from './index.less';

export type ButtonType = 'primary' | 'secondary' | 'outline' | 'ghost' | 'danger' | 'link';
export type ButtonSize = 'small' | 'medium' | 'large';

export interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  /**
   * 按钮类型
   */
  type?: ButtonType;
  /**
   * 按钮大小
   */
  size?: ButtonSize;
  /**
   * 是否禁用
   */
  disabled?: boolean;
  /**
   * 是否加载中
   */
  loading?: boolean;
  /**
   * 按钮图标
   */
  icon?: ReactNode;
  /**
   * 图标位置
   */
  iconPosition?: 'left' | 'right';
  /**
   * 是否为块级元素
   */
  block?: boolean;
  /**
   * 是否为圆形按钮
   */
  round?: boolean;
  /**
   * 子元素
   */
  children?: ReactNode;
  /**
   * 自定义类名
   */
  className?: string;
}

/**
 * 按钮组件
 */
const Button: React.FC<ButtonProps> = ({
  type = 'primary',
  size = 'medium',
  disabled = false,
  loading = false,
  icon,
  iconPosition = 'left',
  block = false,
  round = false,
  children,
  className,
  ...rest
}) => {
  const buttonClasses = classNames(
    styles.button,
    styles[`button-${type}`],
    styles[`button-${size}`],
    {
      [styles['button-disabled']]: disabled || loading,
      [styles['button-loading']]: loading,
      [styles['button-block']]: block,
      [styles['button-round']]: round,
      [styles['button-icon-only']]: !children && icon,
    },
    className
  );

  const renderIcon = () => {
    if (!icon && !loading) return null;
    
    return (
      <span className={styles.icon}>
        {loading ? <Spin size="small" /> : icon}
      </span>
    );
  };

  return (
    <button
      className={buttonClasses}
      disabled={disabled || loading}
      {...rest}
    >
      {iconPosition === 'left' && renderIcon()}
      {children && <span className={styles.content}>{children}</span>}
      {iconPosition === 'right' && renderIcon()}
    </button>
  );
};

export default Button;
