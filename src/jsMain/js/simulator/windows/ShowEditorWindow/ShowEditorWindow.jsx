import React, {
  useEffect,
  useState,
  useCallback,
  useContext,
  useRef,
} from 'react';
import AceEditor from 'react-ace';
import classNames from 'classnames';
import styles from './ShowEditorWindow.scss';
import 'ace-builds/src-noconflict/mode-glsl';
import 'ace-builds/src-noconflict/theme-github';
import 'ace-builds/src-noconflict/theme-tomorrow_night_bright';
import { store } from '../../../store';

const ShowEditorWindow = (props) => {
  const { state } = useContext(store);
  const { sheepSimulator, selectedShow, isConnected } = state;
  const aceEditor = useRef(null);
  const windowRootEl = useRef(null);

  useEffect(() => {
    const ro = new ResizeObserver(() => aceEditor.current.editor.resize());
    ro.observe(windowRootEl.current);
    return () => ro.unobserve(windowRootEl.current);
  }, [windowRootEl]);

  useEffect(() => {
    // Look up the text for the show
    const allShows = sheepSimulator?.shows.toArray() || [];
    const currentShow = allShows.find(({ name }) => name === selectedShow);

    setShowStr(currentShow?.program);
  }, [selectedShow, isConnected]);

  const [showStr, setShowStr] = useState('');
  const onChange = useCallback(
    (newValue) => {
      setShowStr(newValue);
    },
    [setShowStr]
  );

  const previewShow = () => {
    console.log(`previewShow!`);
  };

  return (
    <div className={styles.showEditorWindow} ref={windowRootEl}>
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
      <AceEditor
        ref={aceEditor}
        mode="glsl"
        theme="tomorrow_night_bright"
        width="100%"
        height="100%"
        showGutter={false}
        setAutoScrollEditorIntoView={true}
        onChange={onChange}
        value={showStr}
        name="ShowEditorWindow"
        editorProps={{ $blockScrolling: true }}
      />
    </div>
  );
};
export default ShowEditorWindow;
