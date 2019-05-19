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
        // Question: What is the render function for?
        // Is it to give the shows the ability to render whatever elements
        // they want?
        //
        // If we want to give shows the ability to render elements, can we
        // let react handle the actual rendering? I'd rather have a show
        // define an interface to render as JSON:
        /**
        {
          "showName": "someCoolShow",
          "interface": [
            {
              "type": "text",
              "value": "How Can Mirrors Be Real If Our Eyes Aren't Real"
            },
            {
              "type": "slider",
              "label": "SpArKlE SlIdEr"
              "value": 0,
              "step": 0.001,
              "range": [0, 1]
            }
          ]
        }
        */
        // and then let react handle parsing and rendering the actual elements
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
