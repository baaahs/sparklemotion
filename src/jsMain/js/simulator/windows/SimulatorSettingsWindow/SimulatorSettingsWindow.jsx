import React from 'react';
import {store} from '../../../store';
import {baaahs} from 'sparklemotion';

const SimulatorSettingsWindow = (props) => {
    const {state} = React.useContext(store);
    const Console = baaahs.sim.ui.Console;
    let simulator = state.sheepSimulator?.facade;
    return <Console simulator={simulator}/>;
};
export default SimulatorSettingsWindow;
