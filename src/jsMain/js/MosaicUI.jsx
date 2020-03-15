import { hot } from 'react-hot-loader';
import styles from './MosaicUI.scss';
import React, { useEffect, useContext } from 'react';
import { Mosaic, MosaicWindow, MosaicZeroState } from 'react-mosaic-component';
import SheepVisualizerWindow from './simulator/windows/SheepVisualizerWindow/SheepVisualizerWindow';
import SimulatorSettingsWindow from './simulator/windows/SimulatorSettingsWindow/SimulatorSettingsWindow';
import MosiacMenuBar from './mosiac/MosiacMenuBar/MosiacMenuBar';
import { StateProvider, store } from './store';

const EMPTY_ARRAY = [];
const additionalControls = React.Children.toArray([]);

const WINDOWS_BY_TYPE = {
  'Sheep Visualizer': SheepVisualizerWindow,
  'Simulator Settings': SimulatorSettingsWindow,
};

const MosaicApp = (props) => {
  return (
    <StateProvider>
      <MosaicUI {...props} />
    </StateProvider>
  );
};

const MosaicUI = (props) => {
  const { getSheepSimulator } = props;
  const { state, dispatch } = useContext(store);

  const setState = (statePartial) =>
    dispatch({ type: 'SET_ENTIRE_STATE', payload: statePartial });

  useEffect(() => {
    const sheepSimulator = getSheepSimulator();
    sheepSimulator.start();
    dispatch({ type: 'SET_SHEEP_SIMULATOR', payload: { sheepSimulator } });
  }, []);

  //
  // Mosaic API
  //
  const onChange = (currentNode) => {
    setState({ currentNode });
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
        renderTile={(type, path) => (
          <MosaicWindow
            draggable={false}
            additionalControls={type === 3 ? additionalControls : EMPTY_ARRAY}
            title={type}
            createNode={createNode}
            path={path}
            onDragStart={() => console.log('MosaicWindow.onDragStart')}
            onDragEnd={(type) => console.log('MosaicWindow.onDragEnd', type)}
            renderToolbar={({ title }) => (
              <div className={styles.panelToolbar}>{title}</div>
            )}
          >
            <div className={styles.windowContainer}>
              {React.createElement(WINDOWS_BY_TYPE[type])}
            </div>
          </MosaicWindow>
        )}
        zeroStateView={<MosaicZeroState createNode={createNode} />}
        value={state.currentNode}
        onChange={onChange}
        onRelease={onRelease}
      />
    </div>
  );
};

export default hot(module)(MosaicApp);
