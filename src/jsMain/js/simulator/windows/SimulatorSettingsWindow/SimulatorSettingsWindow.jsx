import React from 'react';
import styles from './SimulatorSettingsWindow.scss';

const SimulatorSettingsWindow = (props) => {
  return (
    <div id="simulatorView" className={styles.simulatorSettings}>
      <table
        id="networkView"
        className="simulatorSection"
        style={{ tableLayout: 'fixed', width: '100%' }}
      >
        <tbody>
          <tr>
            <th colSpan="2" style={{ textAlign: 'left' }}>
              Network
            </th>
          </tr>
          <tr>
            <td>Packet loss rate:</td>
            <td
              id="networkPacketLossRate"
              className={styles.networkPacketLossRate}
            ></td>
          </tr>
          <tr>
            <td>Packets received:</td>
            <td id="networkPacketsReceived"></td>
          </tr>
          <tr>
            <td>Packets dropped:</td>
            <td id="networkPacketsDropped"></td>
          </tr>
        </tbody>
      </table>

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
        <label>
          <input type="checkbox" id="vizRotation" defaultChecked /> Rotate
        </label>
        <div className={styles.pixelCount}>
          Pixel count: <span id="visualizerPixelCount">n/a</span>
        </div>
      </div>
    </div>
  );
};
export default SimulatorSettingsWindow;
