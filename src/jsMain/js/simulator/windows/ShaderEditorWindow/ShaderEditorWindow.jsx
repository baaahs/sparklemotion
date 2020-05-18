import React, {useCallback, useContext, useEffect, useRef, useState,} from 'react';
import AceEditor from 'react-ace';
import {Range} from 'ace-builds';
import classNames from 'classnames';
import styles from './ShaderEditorWindow.scss';
import 'ace-builds/src-noconflict/mode-glsl';
import 'ace-builds/src-noconflict/theme-github';
import 'ace-builds/src-noconflict/theme-tomorrow_night_bright';
import {store} from '../../../store';
import ShowControls from "../../../app/components/Shows/ShowControls";
import {useResizeListener} from '../../../app/hooks/useResizeListener';
import {baaahs} from 'sparklemotion';

const ShaderEditorWindow = (props) => {
  const { state } = useContext(store);
  const { sheepSimulator, selectedShow, isConnected } = state;
  const aceEditor = useRef(null);
  const windowRootEl = useRef(null);
  const canvasContainerEl = useRef(null);
  const statusContainerEl = useRef(null);
  const [glslPreviewer, setGlslPreviewer] = useState(null);
  const [gadgets, setGadgets] = useState([]);
  const [openShaders, setOpenShaders] = useState([]);
  const [extractionCandidate, setExtractionCandidate] = useState(null);
  let glslNumberMarker = null;

  // Anytime the sheepView div is resized,
  // ask the Visualizer to resize the 3D sheep canvas
  useResizeListener(windowRootEl, () => {
    aceEditor.current.editor.resize();
  });

  const updatePreview = (src) => {
    aceEditor.current.editor.getSession().setAnnotations([]);

    glslPreviewer?.setShaderSrc(src, (pGadgets, errors) => {
      aceEditor.current.editor.getSession().setAnnotations(errors.map(e => ({
          row: e.row,
          column: e.column,
          text: e.message,
          type: "error"
        })));

      if (errors.length === 0) {
        setGadgets(pGadgets);

        const renderContext = baaahs.shaders.GlslShader.Companion.globalRenderContext;
        sheepSimulator.switchToShow(new baaahs.shows.GlslShow(selectedShow, src, renderContext, true));
      }
    });
  };

  useEffect(() => {
    // Look up the text for the show
    const allShows = sheepSimulator?.shows.toArray() || [];
    const currentShow = allShows.find(({ name }) => name === selectedShow);

    if (currentShow && !currentShow.isPreview) {
      let shaderSource = currentShow?.src;
      setShowStr(shaderSource);
      updatePreview(shaderSource);

      setOpenShaders([
        ...openShaders,
        { name: currentShow.name, src: currentShow.src, show: currentShow }
      ])
    }
  }, [selectedShow, isConnected, glslPreviewer]);

  const [showStr, setShowStr] = useState('');
  const onChange = useCallback(
    (newValue) => {
      setShowStr(newValue);
      try {
        updatePreview(newValue);
      } catch (e) {
        console.error("Uncaught exception in editor onChange", e);
      }
    },
    [setShowStr, glslPreviewer]
  );

  const glslNumberRegex = /[0-9.]/;
  const glslIllegalRegex = /[A-Za-z_]/;
  const glslFloatRegex = /([0-9]+\.[0-9]*|[0-9]*\.[0-9]+)/;
  const onCursorChange = useCallback(
    (selection) => {
      const session = selection.session;
      if (glslNumberMarker) {
        session.removeMarker(glslNumberMarker);
      }

      const cursor = selection.getCursor();
      const line = session.getDocument().getLine(cursor.row);
      let start = cursor.column;
      let end = cursor.column;
      while (glslNumberRegex.test(line.charAt(start - 1))) start--;
      while (glslNumberRegex.test(line.charAt(end))) end++;
      let badCharBefore = start > 0 && glslIllegalRegex.test(line.charAt(start - 1));
      let badCharAfter = end < line.length - 1 && glslIllegalRegex.test(line.charAt(end));
      const candidate = line.substring(start, end);
      let looksLikeFloatOrInt = glslFloatRegex.test(candidate);
      if (badCharBefore || badCharAfter || !looksLikeFloatOrInt) {
        if (extractionCandidate?.text !== null) setExtractionCandidate({text: null, range: null});
      } else {
        const range = new Range(cursor.row, start, cursor.row, end);
        glslNumberMarker = session.addMarker(range, styles.glslNumber, "text", false);

        if (extractionCandidate?.text !== candidate) {
          setExtractionCandidate({text: candidate, range: range});
        }
      }
    },
    [setShowStr, glslPreviewer]
  );

  useEffect(() => {
    // Pass div to Kotlin GlslPreview class
    const glslPreviewer = new baaahs.glsl.GlslPreview(canvasContainerEl.current, statusContainerEl.current);
    updatePreview(showStr);
    glslPreviewer.start();

    setGlslPreviewer(glslPreviewer);
    return () => {
      // The GlslPreviewWindow is being unmounted, disconnect from the GlslPreview
      glslPreviewer.stop();
      glslPreviewer.destroy();
    }
  }, []);

  const previewShow = () => {
    console.log(`previewShow!`);
  };

  useResizeListener(windowRootEl, () => {
    if (glslPreviewer) {
      // Tell Kotlin controller the window was resized
      glslPreviewer.resize();
    }
  });

  return (
    <div className={styles.shaderEditorWindow} ref={windowRootEl}>
      <div className={styles.toolbar}>
        <div className={styles.showName}>
          <i className="fas fa-chevron-right"></i>
          <input className={styles.showNameInput} defaultValue={selectedShow} />
        </div>
        <div className={styles.buttons}>
          <i
            className={classNames('fas', 'fa-play', styles.iconButton)}
            onClick={previewShow}
          />
        </div>
      </div>
      <div className={styles.previewBar}>
        <div className={styles.preview} ref={canvasContainerEl}/>
        <div className={styles.status} ref={statusContainerEl}/>
        <div className={styles.controls}>
          <ShowControls gadgets={gadgets}/>
        </div>
      </div>
      <AceEditor
        ref={aceEditor}
        mode="glsl"
        theme="tomorrow_night_bright"
        width="100%"
        height="100%"
        showGutter={true}
        setAutoScrollEditorIntoView={true}
        onChange={onChange}
        onCursorChange={onCursorChange}
        value={showStr}
        name="ShaderEditorWindow"
        editorProps={{ $blockScrolling: true }}
      />
      <div>Extract {extractionCandidate?.text}?</div>
    </div>
  );
};
export default ShaderEditorWindow;
