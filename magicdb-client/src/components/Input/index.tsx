import React, { InputHTMLAttributes, ReactNode, forwardRef, ForwardedRef } from 'react';
import classNames from 'classnames';
import styles from './index.less';

export type InputSize = 'small' | 'medium' | 'large';

export interface InputProps extends Omit<InputHTMLAttributes<HTMLInputElement>, 'size'> {
  /**
   * 输入框大小
   */
  size?: InputSize;
  /**
   * 前缀图标
   */
  prefix?: ReactNode;
  /**
   * 后缀图标
   */
  suffix?: ReactNode;
  /**
   * 是否有错误
   */
  error?: boolean;
  /**
   * 是否成功
   */
  success?: boolean;
  /**
   * 是否为块级元素
   */
  block?: boolean;
  /**
   * 自定义类名
   */
  className?: string;
}

/**
 * 输入框组件
 */
const Input = forwardRef(
  (
    {
      size = 'medium',
      prefix,
      suffix,
      error = false,
      success = false,
      block = false,
      className,
      disabled,
      ...rest
    }: InputProps,
    ref: ForwardedRef<HTMLInputElement>
  ) => {
    const inputWrapperClasses = classNames(
      styles.inputWrapper,
      styles[`input-${size}`],
      {
        [styles['input-error']]: error,
        [styles['input-success']]: success,
        [styles['input-disabled']]: disabled,
        [styles['input-block']]: block,
        [styles['input-with-prefix']]: prefix,
        [styles['input-with-suffix']]: suffix,
      },
      className
    );

    return (
      <div className={inputWrapperClasses}>
        {prefix && <div className={styles.prefix}>{prefix}</div>}
        <input
          ref={ref}
          className={styles.input}
          disabled={disabled}
          {...rest}
        />
        {suffix && <div className={styles.suffix}>{suffix}</div>}
      </div>
    );
  }
);

Input.displayName = 'Input';

export default Input;
