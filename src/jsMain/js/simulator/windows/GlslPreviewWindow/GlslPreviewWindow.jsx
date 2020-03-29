import React, {useContext, useEffect, useRef, useState,} from 'react';
import styles from './GlslPreviewWindow.scss';
import {store} from '../../../store';
import {useResizeListener} from '../../../app/hooks/useResizeListener';
import {baaahs} from 'sparklemotion';

const GlslPreviewWindow = () => {
  const { state } = useContext(store);
  const { sheepSimulator } = state;
  const windowRootEl = useRef(null);
  const canvasContainerEl = useRef(null);
  const statusContainerEl = useRef(null);
  const [glslPreviewer, setGlslPreviewer] = useState(null);

  useEffect(
    () => {
        // Pass div to Kotlin GlslPreview class
      // const glslPreviewer = new baaahs.glsl.GlslPreview(canvasContainerEl.current, statusContainerEl.current);
      // glslPreviewer.start();
      // setGlslPreviewer(glslPreviewer);
      return () => {
        // The GlslPreviewWindow is being unmounted, disconnect from the GlslPreview
        // glslPreviewer.stop();
        // glslPreviewer.destroy();
      };
    }, []
    // { canvasContainerEl }
  );

  useResizeListener(windowRootEl, () => {
    if (glslPreviewer) {
        // Tell Kotlin controller the window was resized
        glslPreviewer.resize();
    }
  });

  return (
    <div ref={windowRootEl}>
      <div className={styles.toolbar}>GLSL Toolbar.</div>
      <div className={styles.canvasContainer} ref={canvasContainerEl} />
      <div className={styles.status} ref={statusContainerEl} />
    </div>
  );
};

export default GlslPreviewWindow;
