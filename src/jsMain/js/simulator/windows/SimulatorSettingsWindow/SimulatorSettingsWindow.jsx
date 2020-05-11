import React from 'react';
import {store} from '../../../store';
import styles from './SimulatorSettingsWindow.scss';
import {baaahs} from 'sparklemotion';

const SimulatorSettingsWindow = (props) => {
    const NetworkPanel = baaahs.sim.ui.NetworkPanel;
    const { state } = React.useContext(store);

    console.log("NetworkPanel SimulatorSettingsWindow: store state is ", state);
    return (
    <div id="simulatorView" className={styles.simulatorSettings}>
      <NetworkPanel simulator={state.sheepSimulator}/>

      <table
        id="framerateView"
        className={styles.simulatorSection}
        style={{ tableLayout: 'fixed', width: '100%' }}
      >
        <tbody>
          <tr>
            <th colSpan="2" style={{ textAlign: 'left' }}>
              Effective frame rate
            </th>
            <th>Elapsed</th>
          </tr>
          <tr>
            <td>Show:</td>
            <td id="showFramerate"></td>
            <td id="showElapsedMs"></td>
          </tr>
          <tr>
            <td>&nbsp;&nbsp;average:</td>
            <td id="showAvgFramerate"></td>
            <td id="showAvgElapsedMs"></td>
          </tr>
          <tr>
            <td>Visualizer:</td>
            <td id="visualizerFramerate"></td>
            <td id="visualizerElapsedMs"></td>
          </tr>
          <tr>
            <td>Brains:</td>
            <td id="brainsFramerate">tbd</td>
            <td id="brainsElapsedMs"></td>
          </tr>
        </tbody>
      </table>

      <div className="simulatorSection">
        <b>Pinky:</b>
        <div id="pinkyView"></div>
      </div>

      <div className="simulatorSection">
        <b>Brains:</b>
        <div id="brainsView"></div>
        <div id="brainDetails"></div>
        <div id="selectionInfo"></div>
      </div>

      <div className={styles.controls}>
        <div className={styles.pixelCount}>
          Pixel count: <span id="visualizerPixelCount">n/a</span>
        </div>
      </div>
    </div>
  );
};
export default SimulatorSettingsWindow;
