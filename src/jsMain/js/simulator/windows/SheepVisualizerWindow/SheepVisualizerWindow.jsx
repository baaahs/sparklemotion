import React, {
  useRef,
  useEffect,
  useContext,
  useCallback,
  useState,
} from 'react';
import styles from './SheepVisualizerWindow.scss';
import { store } from '../../../store';
import { FormGroup, FormControlLabel, Switch } from '@material-ui/core';
import {useResizeListener} from "../../../app/hooks/useResizeListener";

const SheepVisualizerWindow = () => {
  const { state } = useContext(store);
  const { sheepSimulator } = state;
  const sheepViewEl = useRef(null);
  const [rotate, setRotate] = useState(
    sheepSimulator?.visualizer.rotate || false
  );

  // Sync the rotate checkbox back to the simulator
  useEffect(() => {
    if (sheepSimulator) sheepSimulator.visualizer.rotate = rotate;
  }, [rotate]);

  // Anytime the sheepView div is resized,
  // ask the Visualizer to resize the 3D sheep canvas
  useResizeListener(sheepViewEl, () => {
    sheepSimulator?.visualizer.resize();
  });

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
      <div id="sheepView" className={styles.sheepView} />
    </div>
  );
};

export default SheepVisualizerWindow;
