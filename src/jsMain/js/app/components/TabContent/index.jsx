import React, { Component } from 'react';
import PropTypes from 'prop-types';
import {
  TAB_OPTION_SHOW_LIST,
  TAB_OPTION_SHOW_INTERFACE,
  WEB_UI_TABS_LIST,
} from '../NavigationTabBar/constants';
import ShowList from '../../ShowList';
import RangeSlider from '../../Slider';
import styles from '../../app.scss';
import ColorPicker from "../../Menu/components/ColorPicker";

class ControlsTabContent extends Component {
  render() {
    const { gadgets } = this.props;

    return (
      <div className={styles.root}>
        {gadgets.map((gadgetInfo) => {
          const { gadget, topicName } = gadgetInfo;
          return (
            <div key={topicName} className={styles['gadget-view']}>
              {this.createGadgetView(gadget)}
            </div>
          );
        })}
      </div>
    );
  }

  createGadgetView = (gadget) => {
    if (gadget instanceof baaahs.gadgets.ColorPicker) {
      return <ColorPicker gadget={gadget} />;
    } else if (gadget instanceof baaahs.gadgets.Slider) {
      return <RangeSlider gadget={gadget} />;
    } else {
      return <div />;
    }
  };
}

const ShowsTabContent = (props) => {
  return <ShowList pubSub={props.pubSub} />;
};

const TabContent = (props) => {
  const { selectedTab } = props;
  debugger;
  switch (selectedTab) {
    case TAB_OPTION_SHOW_LIST:
      return <ShowsTabContent {...props} />;
    case TAB_OPTION_SHOW_INTERFACE:
      return <ControlsTabContent {...props} />;
    default:
      return null;
  }
};

TabContent.propTypes = {
  selectedTab: PropTypes.oneOf(WEB_UI_TABS_LIST),
};

TabContent.defaultProps = {
  selectedTab: null,
};

export default TabContent;
