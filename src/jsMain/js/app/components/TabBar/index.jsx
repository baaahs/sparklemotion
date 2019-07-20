import React from 'react';
import PropTypes from 'prop-types';
import classNames from 'classnames';
import styles from './TabBar.scss';

const TabBar = ({ tabs, activeTab, onTabClick }) => (
  <div className={styles['tab-bar__wrapper']}>
    {tabs.map((tab) => (
      <button
        key={tab}
        className={classNames(styles['tab-bar__button'], {
          [styles['tab-bar__button--active']]: activeTab === tab,
        })}
        onClick={(event) => {
          onTabClick(tab, event);
        }}
      >
        {tab}
      </button>
    ))}
  </div>
);

TabBar.propTypes = {
  tabs: PropTypes.arrayOf(PropTypes.string.isRequired).isRequired,
  activeTab: PropTypes.string,
  onTabClick: PropTypes.func.isRequired,
};

TabBar.defaultProps = {
  activeTab: '',
};

export default TabBar;
