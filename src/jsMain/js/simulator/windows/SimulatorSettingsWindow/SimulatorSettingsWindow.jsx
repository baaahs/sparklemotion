import React from 'react';
import {store} from '../../store';
import {baaahs} from 'sparklemotion';
import {FormControlLabel, Switch} from '@material-ui/core';
import styles from "../SheepVisualizerWindow/SheepVisualizerWindow.scss";

const SimulatorSettingsWindow = (props) => {
  const {state} = React.useContext(store);
  const Console = baaahs.sim.ui.Console;
  const [isOpen, setIsOpen] = React.useState(false);
  const simulator = state.simulator.facade;

  return (
    <div>
      <div className={styles.toolbar}>
        <FormControlLabel
          control={
            <Switch
              size="small"
              checked={isOpen}
              onChange={() => setIsOpen(!isOpen)}
            />
          }
          label="Open"
        />
      </div>

      {isOpen ? <Console simulator={simulator}/> : <div/>}
    </div>
  );
};
export default SimulatorSettingsWindow;
