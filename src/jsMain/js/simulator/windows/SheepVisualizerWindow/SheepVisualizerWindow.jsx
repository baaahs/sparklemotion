import React, { useRef, useEffect, useContext, useCallback } from 'react';
import throttle from 'lodash/throttle';
import styles from './SheepVisualizerWindow.scss';
import ResizeObserver from 'resize-observer-polyfill';
import { store } from '../../../store';

const SheepVisualizerWindow = (props) => {
  const { state } = useContext(store);
  const { sheepSimulator } = state;
  const sheepViewEl = useRef(null);

  // Anytime the sheepView div is resized,
  // ask the Visualizer to resize the 3D sheep canvas
  const onWindowResized = useCallback(
    throttle(() => {
      sheepSimulator.visualizer.resize();
    }, 40),
    [sheepSimulator]
  );

  useEffect(() => {
    if (!sheepSimulator) return;

    const ro = new ResizeObserver((entries, observer) => {
      for (const entry of entries) {
        const { left, top, width, height } = entry.contentRect;
        onWindowResized({ left, top, width, height });
      }
    });
    ro.observe(sheepViewEl.current);

    return () => {
      ro.unobserve(sheepViewEl.current);
    };
  }, [sheepViewEl, sheepSimulator]);

  // Return static HTML for now since the Kotlin code does the DOM manipulation
  return <div id="sheepView" className={styles.sheepView} ref={sheepViewEl} />;
};

export default SheepVisualizerWindow;
