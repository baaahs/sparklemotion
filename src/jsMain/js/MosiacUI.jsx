import { hot } from 'react-hot-loader';
import React from 'react';
import Simulator from './simulator';

const MosiacUI = (props) => {
  // In the future, this will have many panels,
  // but for now, just render the simulator
  return <Simulator />;
};

export default hot(module)(MosiacUI);
