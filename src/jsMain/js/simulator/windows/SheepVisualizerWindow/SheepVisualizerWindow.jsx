import React, {useContext, useEffect, useRef, useState,} from 'react';
import styles from './SheepVisualizerWindow.scss';
import {store} from '../../../store';
import {FormControlLabel, Switch} from '@material-ui/core';
import {useResizeListener} from "../../../app/hooks/useResizeListener";
import {baaahs} from 'sparklemotion';

const SheepVisualizerWindow = () => {
  const {state} = useContext(store);
  const VisualizerPanel = baaahs.visualizer.ui.VisualizerPanel;
  const simulator = state.sheepSimulator?.facade;
  const sheepViewEl = useRef(null);
  const visualizer = simulator?.visualizer;

  const [rotate, setRotate] = useState(visualizer?.rotate || false);

  // Sync the rotate checkbox back to the simulator
  useEffect(() => {
    if (visualizer) visualizer.rotate = rotate;
  }, [rotate]);

  // Anytime the sheepView div is resized,
  // ask the Visualizer to resize the 3D sheep canvas
  useResizeListener(sheepViewEl, () => {
    visualizer?.resize();
  });

  if (!simulator) return "Loading...";

  return (
    <div ref={sheepViewEl}>
      <div className={styles.toolbar}>
        <FormControlLabel
          control={
            <Switch
              size="small"
              checked={rotate}
              onChange={() => setRotate((rotate) => !rotate)}
            />
          }
          label="Rotate"
        />
      </div>

      <VisualizerPanel visualizer={visualizer}/>
    </div>
  );
};

export default SheepVisualizerWindow;
