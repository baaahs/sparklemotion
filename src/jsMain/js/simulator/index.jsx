import React, { Component } from 'react';

const Simulator = (props) => {
  // Return static HTML for now since the Kotlin code does the DOM manipulation
  return (
    <div id="simulatorRoot">
      <div id="sheepView"></div>

      <div id="simulatorView">
        <h3>BAAAHS Simulator FUCK YEAH</h3>

        <div id="launcher" className="simulatorSection"></div>

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
              <td id="networkPacketLossRate"></td>
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
          className="simulatorSection"
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

        <div className="visualizerSection">
          <b>Visualizer:</b>
          <label>
            <input type="checkbox" id="vizRotation" defaultChecked /> Rotate
          </label>
          <div id="selectionInfo"></div>
          <div>
            Pixel count: <span id="visualizerPixelCount">n/a</span>
          </div>
        </div>

        <div className="simulatorSection">
          <b>Brains:</b>
          <div id="brainsView"></div>
          <div id="brainDetails"></div>
        </div>
      </div>

      <select id="panelSelect"></select>
      <div id="info2"></div>
    </div>
  );
};

export default Simulator;
