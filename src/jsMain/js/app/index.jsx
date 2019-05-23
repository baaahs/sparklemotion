import React, {Component, Fragment} from 'react';
import ColorPicker from './Menu/components/ColorPicker';
import ShowList from './ShowList';
import Slider from './Slider';
import PropTypes from 'prop-types';

import NavigationTabBar from './components/NavigationTabBar';
import TabContent from './components/TabContent';

import {TAB_OPTION_SHOW_LIST} from './components/NavigationTabBar/constants';

const baaahs = sparklemotion.baaahs;

export default class App extends Component {
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
          if (gadget instanceof baaahs.gadgets.ColorPicker) {
            return <ColorPicker key={topicName} gadget={gadget} />;
          } else if (gadget instanceof baaahs.gadgets.Slider) {
            return <Slider key={topicName} gadget={gadget} />;
          } else {
            return <div />;
          }
        })}
        <ShowList pubSub={this.props.pubSub} />
      </Fragment>
    );
  }
}

App.propTypes = {
  pubSub: PropTypes.object.isRequired,
};
