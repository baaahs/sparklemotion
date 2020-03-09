import React, { useRef, useEffect, useContext, useCallback } from 'react';
import throttle from 'lodash/throttle';
import styles from './ClientDeviceWindow.scss';
import ResizeObserver from 'resize-observer-polyfill';
import { store } from '../../../store';

const ClientDeviceWindow = (props) => {
  const { state } = useContext(store);
  const { sheepSimulator } = state;

  return (
    <div className={styles.sheepView}></div>
  );
};

export default ClientDeviceWindow;
