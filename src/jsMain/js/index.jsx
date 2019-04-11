import React from 'react';
import ReactDOM from 'react-dom';

import App from './app';

document.createUiApp = (elementId, uiContext) => {
  let app = <App uiContext={uiContext}/>;
  ReactDOM.render(app, document.getElementById('uiView1'));
  return app;
};

module.hot.accept();
