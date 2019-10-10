import {hot} from 'react-hot-loader';
import React, {Component, Fragment} from 'react';
import PropTypes from 'prop-types';
import TabBar from './components/TabBar';
import Shows from './components/Shows';
import Eyes from './components/Eyes';

import {EYE_CONTROLS, MAIN_TABS, MODAL_PORTAL_DOM_NODE_ID, SHOWS,} from './constants';

const baaahs = sparklemotion.baaahs;

class App extends Component {
  constructor(props) {
    super(props);

    this.state = {
      showPickerOpen: false,
      selectedTab: SHOWS,
      isConnected: this.props.pubSub.isConnected,
    };

    this.pubSubStateChangeListener = function() {
    }.bind(this);
  }

  componentDidMount() {
    this.props.pubSub.addStateChangeListener(this.onPubSubStateChange);
  }

  componentWillUnmount() {
    this.props.pubSub.removeStateChangeListener(this.onPubSubStateChange);
  }

  onPubSubStateChange = () => {
    this.setState({ isConnected: this.props.pubSub.isConnected });
  };

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
          Connecting...
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
