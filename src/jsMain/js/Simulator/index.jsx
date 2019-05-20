import React from 'react';

import Visualizer from '../Visualizer';
import LauncherPanel from './LauncherPanel';

class Simulator extends React.Component {
  constructor(props, context) {
    super(props, context);

    this.sheepSimulator = new sparklemotion.baaahs.SheepSimulator();
    // this.state = {
    //   networkStats: this.sheepSimulator.network.stats,
    // };
  }

  componentDidMount() {
    this.sheepSimulator.start();

    // this.sheepSimulator.network.onStatsUpdate((stats) => {
    //   this.setState({ networkStats: stats });
    // });
  }

  render() {
    return (
      <div>
        <Visualizer sheepSimulator={this.sheepSimulator} />
        <LauncherPanel sheepSimulator={this.sheepSimulator} />

        {/*<NetworkStats stats={this.state.networkStats} />*/}
        {/*<StatusPanel pinky={this.sheepSimulator.pinky}/>*/}
      </div>
    );
  }
}

export default Simulator;
