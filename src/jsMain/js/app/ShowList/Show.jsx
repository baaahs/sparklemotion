import React from 'react';
import classNames from 'classnames';
import PropTypes from 'prop-types';
import styles from './ShowList.scss';

const Show = ({ name, isSelected, handleSelectShow }) => (
  <li
    onClick={() => {
      handleSelectShow(name);
    }}
    className={classNames({
      [styles['selected']]: isSelected,
    })}
  >
    {name}
  </li>
);

Show.propTypes = {
  name: PropTypes.string.isRequired,
};

export default Show;
