import React from 'react';
import {store} from '../../store';
import {baaahs} from 'sparklemotion';
import {FormControlLabel, Switch} from '@material-ui/core';
import styles from "../SheepVisualizerWindow/SheepVisualizerWindow.scss";

const SimulatorSettingsWindow = (props) => {
  const {state} = React.useContext(store);
  const Console = baaahs.sim.ui.Console;
  const GeneratedGlslPalette = baaahs.sim.ui.GeneratedGlslPalette;
  const [isConsoleOpen, setIsConsoleOpen] = React.useState(false);
  const [isGlslPaletteOpen, setIsGlslPaletteOpen] = React.useState(false);
  const simulator = state.simulator.facade;

  return (
    <div>
      <div className={styles.toolbar}>
        <FormControlLabel
          control={
            <Switch
              size="small"
              checked={isConsoleOpen}
              onChange={() => setIsConsoleOpen(!isConsoleOpen)}
            />
          }
          label="Open"
        />

        <FormControlLabel
          control={
            <Switch
              size="small"
              checked={isGlslPaletteOpen}
              onChange={() => setIsGlslPaletteOpen(!isGlslPaletteOpen)}
            />
          }
          label="Show GLSL"
        />
      </div>

      {isConsoleOpen ? <Console simulator={simulator}/> : <div/>}
      {isGlslPaletteOpen
          ? <GeneratedGlslPalette pinky={simulator.pinky} onClose={setIsGlslPaletteOpen(false)}/>
          : <div/>}
    </div>
  );
};
export default SimulatorSettingsWindow;
