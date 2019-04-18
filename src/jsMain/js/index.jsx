import React from 'react';
import ReactDOM from 'react-dom';

import App from './app';
import FakeClientDevice from './FakeClientDevice';
import styles from "./FakeClientDevice.scss";

document.createUiApp = (elementId, uiContext) => {
  let app = <App uiContext={uiContext}/>;
  ReactDOM.render(app, document.getElementById('uiView1'));
  return app;
};

document.createFakeClientDevice = (name, content, onClose, onResize) => {
  let device = <FakeClientDevice width={1024} height={768} content={content} onClose={onClose} onResize={onResize}/>;
  let containerDiv = document.createElement("div");
  document.body.appendChild(containerDiv);

  ReactDOM.render(device, containerDiv, () => {
    let contentHolder = containerDiv.getElementsByClassName(styles['FakeClientDevice--content'])[0];
    contentHolder.appendChild(content);
    content.style.width = contentHolder.offsetWidth;
    content.style.height = contentHolder.offsetHeight;
  });

  return device;
};

module.hot.accept();
