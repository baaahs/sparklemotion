import React, {
  useRef,
  useEffect,
  useContext,
  useCallback,
  useState,
} from 'react';
import throttle from 'lodash/throttle';
import styles from './SheepVisualizerWindow.scss';
import ResizeObserver from 'resize-observer-polyfill';
import { store } from '../../../store';
import { FormGroup, FormControlLabel, Switch } from '@material-ui/core';

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
  const onWindowResized = useCallback(
    throttle(() => {
      sheepSimulator?.visualizer.resize();
    }, 40),
    [sheepSimulator]
  );

  useEffect(() => {
    if (!sheepSimulator) return;

    const ro = new ResizeObserver(onWindowResized);
    ro.observe(sheepViewEl.current);

    return () => {
      ro.unobserve(sheepViewEl.current);
    };
  }, [sheepViewEl, sheepSimulator, onWindowResized]);

  useEffect(() => {
    const intervalId = setTimeout(() => {
      onWindowResized();
    }, 500);

    return () => {
      clearTimeout(intervalId);
    };
  }, [sheepViewEl, onWindowResized]);

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
