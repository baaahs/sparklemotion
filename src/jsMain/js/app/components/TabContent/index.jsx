import React from 'react';
import PropTypes from 'prop-types';
import {
  TAB_OPTION_SHOW_LIST,
  TAB_OPTION_SHOW_INTERFACE,
  TAB_OPTION_COLOR_PICKER,
  WEB_UI_TABS_LIST,
} from '../NavigationTabBar/constants';

const TabContent = ({ selectedTab }) => {
  switch (selectedTab) {
    case TAB_OPTION_SHOW_LIST:
      return <div>TODO: tab content</div>;
    case TAB_OPTION_SHOW_INTERFACE:
      return <div>TODO: Show interface tab</div>;
    case TAB_OPTION_COLOR_PICKER:
      return <div>TODO: COlor picker tabbbbbb</div>;
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
