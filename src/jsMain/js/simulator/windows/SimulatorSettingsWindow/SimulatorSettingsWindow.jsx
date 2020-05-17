import React from 'react';
import {store} from '../../../store';
import {baaahs} from 'sparklemotion';

const SimulatorSettingsWindow = (props) => {
    const Console = baaahs.sim.ui.Console;
    const {state} = React.useContext(store);
    let simulator = state.sheepSimulator?.facade;
    return <Console simulator={simulator}/>;
};
export default SimulatorSettingsWindow;
