import { hot } from 'react-hot-loader';
import React, { Component, Fragment } from 'react';
import PropTypes from 'prop-types';
import TabBar from './components/TabBar';
import Shows from './components/Shows';
import Eyes from './components/Eyes';
import modalStyles from './components/Modal/Modal.scss';

import {
  SHOWS,
  EYE_CONTROLS,
  MAIN_TABS,
  MODAL_PORTAL_DOM_NODE_ID,
} from './constants';

const baaahs = sparklemotion.baaahs;

class App extends Component {
  constructor(props) {
    super(props);

    this.state = {
      showPickerOpen: false,
      selectedTab: SHOWS,
      isConnected: props.pubSub.isConnected,
    };

    props.pubSub.addStateChangeListener(function() {
      this.setState({ isConnected: pubSub.isConnected });
    }.bind(this));
  }

  close = () => {
    console.log('app closed!');
  };

  handleTabClick = (tab) => {
    this.setState({ selectedTab: tab });
  };

  renderContent = (tab) => {
    if (tab === SHOWS) {
      return <Shows pubSub={this.props.pubSub} />;
    } else if (tab === EYE_CONTROLS) {
      return <Eyes pubSub={this.props.pubSub} />;
    }

    console.warn(
      `Warning: Attempting to render tab content that does not exist for tab:`,
      tab
    );

    return null;
  };

  render() {
    const { pubSub } = this.props;
    const { selectedTab, isConnected } = this.state;

    return (
      <Fragment>
        <div id="errorMessage" style={{
          height: "5vh",
          width: "100%",
          backgroundColor: "pink",
          color: "black",
          textAlign: "center",
          fontSize: "2em",
          display: isConnected ? "none" : "block"
        }}>
          Lost connection, reconnecting…
        </div>
        <TabBar
          tabs={MAIN_TABS}
          activeTab={selectedTab}
          onTabClick={this.handleTabClick}
        />
        {this.renderContent(selectedTab)}
        <div id={MODAL_PORTAL_DOM_NODE_ID} />
      </Fragment>
    );
  }
}

App.propTypes = {
  pubSub: PropTypes.object.isRequired,
};

export default hot(module)(App);
