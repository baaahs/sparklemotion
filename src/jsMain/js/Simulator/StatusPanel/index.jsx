import React, { Fragment } from 'react';
import FakeClientDevice from '../FakeClientDevice';

// todo: real interface with WebUi and Mapper implementors
// interface HostedWebApp {
//     fun render(node);
//     fun onResize(node);
//     fun onClose(node);
// }

let nextHostedWebAppId = 0;
let hostedWebApps = {};

class StatusPanel extends React.Component {
  state = {
    hostedWebAppIds: [],
    hostedWebApps: {},
  };

  handleWebUiClick = () => {
    const thisId = nextHostedWebAppId++;

    const webUi = {
      id: thisId,
      // app: new WebUI(),

      onClose: function() {
        console.log('closed!');
      },
      render: function(node) {
        // Explanation behind what this render function is for:
        // https://github.com/baaahs/sparklemotion/pull/70#discussion_r285393649

        node.innerText = 'here i am!';
      },
      onResize: function(node) {},
    };

    hostedWebApps[thisId] = webUi;

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

export default StatusPanel;
