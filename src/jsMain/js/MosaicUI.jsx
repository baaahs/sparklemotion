import {hot} from 'react-hot-loader';
import styles from './MosaicUI.scss';
import {baaahs} from 'sparklemotion';
import React, {useContext, useEffect, useState} from 'react';
import {Mosaic, MosaicWindow, MosaicZeroState} from 'react-mosaic-component';
import SheepVisualizerWindow from './simulator/windows/SheepVisualizerWindow/SheepVisualizerWindow';
import SimulatorSettingsWindow from './simulator/windows/SimulatorSettingsWindow/SimulatorSettingsWindow';
import MosiacMenuBar from './mosiac/MosiacMenuBar/MosiacMenuBar';
import {StateProvider, store} from './store';
import GlslPreviewWindow from "./simulator/windows/GlslPreviewWindow/GlslPreviewWindow";

const EMPTY_ARRAY = [];
const additionalControls = React.Children.toArray([]);

const MosaicApp = (props) => {
  return (
    <StateProvider>
      <MosaicUI {...props} />
    </StateProvider>
  );
};

const MosaicUI = (props) => {
  const WINDOWS_BY_TYPE = {
    'Shader Editor': baaahs.ui.ShaderEditorWindow,
    'Sheep Visualizer': SheepVisualizerWindow,
    'Simulator Settings': SimulatorSettingsWindow,
    'Glsl Preview': GlslPreviewWindow,
  };

  const { getSheepSimulator } = props;
  const { state, dispatch } = useContext(store);
  const [pubSub, setPubSub] = useState(null);

  useEffect(() => {
    if (state.sheepSimulator) return;

    const sheepSimulator = getSheepSimulator();
    setPubSub(sheepSimulator.getPubSub());
    sheepSimulator.start();

    dispatch({ type: 'SET_SHEEP_SIMULATOR', payload: { sheepSimulator } });
  }, []);

  useEffect(() => {
    if (!pubSub) return;

    const onPubSubStateChange = () => {
      dispatch({
        type: 'SET_STATE',
        payload: { isConnected: pubSub.isConnected },
      });
    };
    pubSub.addStateChangeListener(onPubSubStateChange);

    const selectedShowChannel = pubSub.subscribe(
      baaahs.Topics.selectedShow,
      (selectedShow) => {
        dispatch({ type: 'SET_STATE', payload: { selectedShow } });
      }
    );

    return () => {
      selectedShowChannel.unsubscribe();
      pubSub.removeStateChangeListener(onPubSubStateChange);
    };
  }, [pubSub]);

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
        className={'mosaic mosaic-blueprint-theme bp3-dark'}
      />
    </div>
  );
};

export default hot(module)(MosaicApp);
