import React, { memo } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import logo from '@/assets/logo/logo.webp';

interface IProps extends React.DetailedHTMLProps<React.HTMLAttributes<HTMLDivElement>, HTMLDivElement> {
  className?: any;
  size?: number;
  variant?: 'default' | 'cursor';
}

/**
 * 品牌 Logo 组件
 *
 * @param variant 'default' - 使用图片 logo, 'cursor' - 使用类似 Cursor 的实现风格
 */
export default memo<IProps>(({ className, size = 48, variant = 'default', ...res }) => {
  if (variant === 'cursor') {
    return (
      <div
        {...res}
        className={classnames(className, styles.cursorBox)}
        style={{ height: `${size}px`, width: `${size}px` }}
      >
        <div className={styles.cursorLogo}>
          <span className={styles.letter}>M</span>
          <div className={styles.cursor}></div>
        </div>
      </div>
    );
  }

  return (
    <div {...res} className={classnames(className, styles.box)} style={{ height: `${size}px`, width: `${size}px` }}>
      <img src={logo} alt="MagicDB Logo" />
    </div>
  );
});
