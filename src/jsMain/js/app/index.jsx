import React, { Component, Fragment } from 'react';
import ColorPicker from './Menu/components/ColorPicker';
import ShowList from './ShowList';
import Slider from './Slider';

import NavigationTabBar from './components/NavigationTabBar';
import TabContent from './components/TabContent';

import { TAB_OPTION_SHOW_LIST } from './components/NavigationTabBar/constants';

class App extends Component {
  constructor(props) {
    super(props);

    this.state = {
      primaryColor: sparklemotion.baaahs.Color.Companion.WHITE,
      selectedTab: TAB_OPTION_SHOW_LIST,
    };

    this.pubSub = props.uiContext.pubSub;
  }

  componentDidMount() {
    this.subscribeToChannels();
  }

  subscribeToChannels() {
    this.primaryColorChannel = this.pubSub.subscribe(
      sparklemotion.baaahs.Topics.primaryColor,
      (primaryColor) => {
        console.log('received updated primary color!', primaryColor, this);
        this.setState({ primaryColor });
      }
    );
  }

  colorChanged = (color) => {
    console.log('primary color selected!', color, this);
    this.primaryColorChannel.onChange(
      sparklemotion.baaahs.Color.Companion.fromString(color)
    );
  };

  close = () => {
    console.log('app closed!');
  };

  onSelectTab = (selectedTab) => {
    this.setState({ selectedTab });
  };

  render() {
    const { selectedTab } = this.state;

    return (
      <Fragment>
        <NavigationTabBar
          selectedTab={selectedTab}
          onSelectTab={this.onSelectTab}
        />
        <TabContent selectedTab={selectedTab} />
        <ColorPicker
          chosenColor={this.state.primaryColor}
          onColorSelect={this.colorChanged}
        />
        <Slider
          pubSub={this.pubSub}
        />
        <ShowList
          pubSub={this.pubSub}
        />
      </Fragment>
    );
  }
}

export default App;
