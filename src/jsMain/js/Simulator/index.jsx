import React from 'react';

import Visualizer from '../Visualizer';
import StatusPanel from './StatusPanel';

class Simulator extends React.Component {

    constructor(props, context) {
        super(props, context);

        this.sheepSimulator = new sparklemotion.baaahs.SheepSimulator();
    }

    componentDidMount() {
        this.sheepSimulator.start();
    }

    render() {
        return (
            <div>
                <Visualizer model={this.sheepSimulator.visualizer}/>
                <StatusPanel/>
            </div>
        );
    }
}

export default Simulator;