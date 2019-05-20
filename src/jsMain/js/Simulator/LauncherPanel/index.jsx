import React, { Fragment } from 'react';
import FakeClientDevice from '../FakeClientDevice';
import ReactDOM from 'react-dom';
import App from '../../app';

const baaahs = sparklemotion.baaahs;

let nextHostedWebAppId = 0;
let hostedWebApps = {};

class WebUiApp {
  constructor(network, pinkyAddress) {
    const link = network.link();
    this.pubSub = new baaahs.PubSub.Client(
      link,
      pinkyAddress,
      baaahs.proto.Ports.Companion.PINKY_UI_TCP
    );
    this.pubSub.install(baaahs.gadgetModule);
  }

  onRender(node) {
    ReactDOM.render(<App pubSub={this.pubSub} />, node);
  }

  onResize() {}

  onClose() {}
}

class LauncherPanel extends React.Component {
  state = {
    hostedWebAppIds: [],
  };

  handleWebUiClick = () => {
    const thisId = nextHostedWebAppId++;

    hostedWebApps[thisId] = new WebUiApp(
      this.props.sheepSimulator.network,
      this.props.sheepSimulator.pinky.address
    );

    this.setState({
      hostedWebAppIds: [...this.state.hostedWebAppIds, thisId],
    });
  };

  handleCloseDevice = (id) => {
    const newHostedWebAppIds = this.state.hostedWebAppIds.filter((appId) => {
      return appId !== id;
    });

    this.setState({ hostedWebAppIds: newHostedWebAppIds });

    hostedWebApps[id].onClose();
  };

  handleCreateMapperView = () => {
    console.log('craete mapper view');
  };

  render() {
    return (
      <Fragment>
        <button onClick={this.handleWebUiClick}>Web UI</button>
        <button onClick={this.handleCreateMapperView}> Mapper</button>
        {this.state.hostedWebAppIds.map((id) => {
          return (
            <FakeClientDevice
              key={id}
              id={id}
              hostedWebApp={hostedWebApps[id]}
              onClose={() => {
                this.handleCloseDevice(id);
              }}
            />
          );
        })}
      </Fragment>
    );
  }
}

export default LauncherPanel;
