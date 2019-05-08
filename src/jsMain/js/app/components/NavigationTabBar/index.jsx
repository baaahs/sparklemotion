import React from 'react';
import PropTypes from 'prop-types';
import { WEB_UI_TABS_LIST } from './constants';

const NavigationTabBar = (props) => {
  const { selectedTab, onSelectTab } = props;

  return (
    <nav>
      {WEB_UI_TABS_LIST.map((tab) => (
        <button
          key={tab}
          onClick={() => {
            onSelectTab(tab);
          }}
        >
          {tab}
        </button>
      ))}
    </nav>
  );
};

NavigationTabBar.propTypes = {
  selectedTab: PropTypes.oneOf(WEB_UI_TABS_LIST),
  onSelectTab: PropTypes.func,
};

NavigationTabBar.defaultProps = {
  selectedTab: null,
  onSelectTab: () => {
    console.log('Warning: onSelectTab function is not implemented or hooked up')
  },
};

export default NavigationTabBar;
