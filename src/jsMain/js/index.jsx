import React from 'react';
import ReactDOM from 'react-dom';

import App from './app';
import FakeClientDevice from './FakeClientDevice';
import * as THREE from 'three';
import CameraControls from 'camera-controls';

document.createUiApp = (pubSubClient) => {
  return { // HostedWebApp
    render: (parentNode) => {
      const app = <App pubSub={pubSubClient}/>;
      ReactDOM.render(app, parentNode);
    },

    onClose: () => {
    },
  };
};

document.createFakeClientDevice = (name, hostedWebApp) => {
  let containerDiv = document.createElement('div');

  let onClose = () => {
    document.body.removeChild(containerDiv);
    hostedWebApp.onClose();
  };

  let device = (
    <FakeClientDevice
      width={1024}
      height={768}
      hostedWebApp={hostedWebApp}
      onClose={onClose}
    />
  );

  document.body.appendChild(containerDiv);

  ReactDOM.render(device, containerDiv);
};

CameraControls.install( { THREE: THREE } );

document.createCameraControls = (camera, element) => {
  return new CameraControls(camera, element);
};

module.hot.accept();
