import React, {
  useRef,
  useEffect,
  useContext,
  useState,
} from 'react';
import styles from './GlslPreviewWindow.scss';
import { store } from '../../../store';
import { FormGroup, FormControlLabel, Switch } from '@material-ui/core';
import { useResizeListener } from '../../../app/hooks/useResizeListener';

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

  useResizeListener(windowRootEl, () => {
    // if (!glslPreviewer) return;
    // Tell Kotlin controller the window was resized
    // glslPreviewer.resize();
  });

  return (
    <div ref={windowRootEl}>
      <div className={styles.toolbar}>GLSL Toolbar.</div>
      <div className={styles.canvasContainer} ref={canvasContainerEl} />
    </div>
  );
};

export default GlslPreviewWindow;
