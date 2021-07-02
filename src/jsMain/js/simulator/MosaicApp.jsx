import styles from './MosaicUI.scss';
import React, {useContext} from 'react';
import {Mosaic, MosaicWindow, MosaicZeroState} from 'react-mosaic-component';
import SheepVisualizerWindow from './windows/SheepVisualizerWindow/SheepVisualizerWindow';
import SimulatorSettingsWindow from './windows/SimulatorSettingsWindow/SimulatorSettingsWindow';
import MosiacMenuBar from '../mosiac/MosiacMenuBar/MosiacMenuBar';
import {StateProvider, store} from './store';
import {baaahs} from 'sparklemotion';

const EMPTY_ARRAY = [];
const additionalControls = React.Children.toArray([]);

const MosaicApp = (props) => {
  return (
    <StateProvider simulator={props.simulator}>
      <MosaicUI {...props} />
    </StateProvider>
  );
};

const MosaicUI = (props) => {
  const WINDOWS_BY_TYPE = {
    'Sheep Visualizer': SheepVisualizerWindow,
    'Simulator Console': SimulatorSettingsWindow,
  };

  const { state, dispatch } = useContext(store);

  //
  // Mosaic API
  //
  const onChange = (currentNode) => {
    dispatch({ type: 'SET_STATE', payload: { currentNode } });
  };
  const onRelease = (currentNode) => {
    console.log('Mosaic.onRelease():', currentNode);
  };
  const createNode = (id) => {
    console.log(`createNode`);
  };

  // In the future, this will have many windows,
  // but for now, just render the simulator
  return (
    <div
      style={{
        width: '100%',
        height: '100%',
        display: 'flex',
        flexDirection: 'column',
        position: 'absolute',
        top: 0,
        left: 0,
      }}
    >
      <MosiacMenuBar />
      <Mosaic
        renderTile={(type, path) => {
          let windowType = WINDOWS_BY_TYPE[type];
          let childElement = (windowType != null)
              ? React.createElement(windowType)
              : React.createElement(baaahs.sim.ui.WebClientWindow, {hostedWebApp: props.hostedWebApp});

          return (
            <MosaicWindow
              draggable={false}
              additionalControls={type === 3 ? additionalControls : EMPTY_ARRAY}
              title={type}
              createNode={createNode}
              path={path}
              onDragStart={() => console.log('MosaicWindow.onDragStart')}
              onDragEnd={(type) => console.log('MosaicWindow.onDragEnd', type)}
              renderToolbar={({title}) => (
                <div className={styles.panelToolbar}>{title}</div>
              )}
            >
              <div className={styles.windowContainer}>
                {childElement}
              </div>
            </MosaicWindow>
            );
        }}
        zeroStateView={<MosaicZeroState createNode={createNode} />}
        value={state.currentNode}
        onChange={onChange}
        onRelease={onRelease}
        className={'mosaic mosaic-blueprint-theme bp3-dark'}
      />
    </div>
  );
};

export default MosaicApp;
