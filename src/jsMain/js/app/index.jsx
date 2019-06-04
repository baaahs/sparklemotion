import { hot } from 'react-hot-loader';
import React, {Component, Fragment} from 'react';
import ColorPicker from './Menu/components/ColorPicker';
import ShowList from './ShowList';
import Slider from './Slider';
import PropTypes from 'prop-types';

import NavigationTabBar from './components/NavigationTabBar';
import TabContent from './components/TabContent';

import {TAB_OPTION_SHOW_LIST} from './components/NavigationTabBar/constants';
import styles from './app.scss';

const baaahs = sparklemotion.baaahs;

class App extends Component {
  constructor(props) {
    super(props);

    this.state = {
      primaryColor: baaahs.Color.Companion.WHITE,
      selectedTab: TAB_OPTION_SHOW_LIST,
      gadgets: [],
    };

    this.pubSub = props.pubSub;
  }

  componentDidMount() {
    this.subscribeToChannels();
  }

  subscribeToChannels() {
    this.gadgetDisplay = baaahs.GadgetDisplay(
      this.props.pubSub,
      (newGadgets) => {
        console.log('got new gadgets!', newGadgets);
        this.setState({ gadgets: newGadgets });
      }
    );
  }

  close = () => {
    console.log('app closed!');
  };

  onSelectTab = (selectedTab) => {
    this.setState({ selectedTab });
  };

  render() {
    const { selectedTab, gadgets } = this.state;

    return (
      <Fragment>
        <NavigationTabBar
          selectedTab={selectedTab}
          onSelectTab={this.onSelectTab}
        />
        <TabContent selectedTab={selectedTab} />
        {gadgets.map((gadgetInfo) => {
          const { gadget, topicName } = gadgetInfo;
          return <div key={topicName} className={styles['gadget-view']}>{this.createGadgetView(gadget)}</div>;
        })}
        <ShowList pubSub={this.props.pubSub} />
      </Fragment>
    );
  }

  createGadgetView = (gadget) => {
    if (gadget instanceof baaahs.gadgets.ColorPicker) {
      return <ColorPicker gadget={gadget}/>;
    } else if (gadget instanceof baaahs.gadgets.Slider) {
      return <Slider gadget={gadget}/>;
    } else {
      return <div/>;
    }
  }
}

App.propTypes = {
  pubSub: PropTypes.object.isRequired,
};

export default hot(module)(App);
