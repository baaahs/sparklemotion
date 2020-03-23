import React, {
  useRef,
  useEffect,
  useContext,
  useCallback,
  useState,
} from 'react';
import throttle from 'lodash/throttle';
import styles from './GlslPreviewWindow.scss';
import ResizeObserver from 'resize-observer-polyfill';
import { store } from '../../../store';
import { FormGroup, FormControlLabel, Switch } from '@material-ui/core';

const GlslPreviewWindow = () => {
  const { state } = useContext(store);
  const { sheepSimulator } = state;
  const windowRootEl = useRef(null);
  const canvasContainerEl = useRef(null);
  const [glslPreviewer, setGlslPreviewer] = useState(null);

  useEffect(
    () => {
      const div = canvasContainerEl.current;

      // Pass div to Kotlin GlslPreview class
      // const glslPreviewer = new sparklemotion.GlslPreview(div);
      // glslPreviewer.start();
      // setGlslPreviewer(glslPreviewer);
      return () => {
        // The GlslPreviewWindow is being unmounted, disconnect from the GlslPreview
        // previewRef.destroy();
      };
    },
    { canvasContainerEl }
  );

  // Anytime the sheepView div is resized,
  // ask the Visualizer to resize the 3D sheep canvas
  const onWindowResized = useCallback(
    throttle(() => {
      // if (!glslPreviewer) return;

      // Tell Kotlin controller the window was resized
      // glslPreviewer.resize();
    }, 40),
    [sheepSimulator, glslPreviewer]
  );

  useEffect(() => {
    if (!sheepSimulator) return;

    const ro = new ResizeObserver(onWindowResized);
    ro.observe(windowRootEl.current);

    return () => {
      ro.unobserve(windowRootEl.current);
    };
  }, [windowRootEl, onWindowResized]);

  return (
    <div ref={windowRootEl}>
      <div className={styles.toolbar}>GLSL Toolbar.</div>
      <div className={styles.canvasContainer} ref={canvasContainerEl} />
    </div>
  );
};

export default GlslPreviewWindow;
