import styles from './MosiacMenuBar.scss';
import React from 'react';

const MosiacMenuBar = () => {
  return (
    <div className={styles.menuBar}>
      <div className={styles.title}>Sparkle Motion</div>
      <div id="launcher" className={styles.menu}>
          {/*Content filled in by Launcher.kt */}
      </div>
    </div>
  );
};

export default MosiacMenuBar;
