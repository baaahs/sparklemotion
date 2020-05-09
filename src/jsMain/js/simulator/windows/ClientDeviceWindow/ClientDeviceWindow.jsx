import React, { useRef, useEffect, useContext, useCallback } from 'react';
import styles from './ClientDeviceWindow.scss';
import { store } from '../../../store';

const ClientDeviceWindow = (props) => {
  const { state } = useContext(store);
  const { sheepSimulator } = state;

  return (
    <div className={styles.sheepView}></div>
  );
};

export default ClientDeviceWindow;
