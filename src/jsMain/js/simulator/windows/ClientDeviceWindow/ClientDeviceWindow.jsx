import React, {useContext} from 'react';
import styles from './ClientDeviceWindow.scss';
import {store} from '../../store';

const ClientDeviceWindow = (props) => {
  const { state } = useContext(store);

  return (
    <div className={styles.sheepView}></div>
  );
};

export default ClientDeviceWindow;
