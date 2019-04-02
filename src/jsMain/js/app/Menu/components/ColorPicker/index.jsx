import React, { Component } from 'react';
import PropTypes from 'prop-types';
import classNames from 'classnames';
import { ChromePicker } from 'react-color';

import styles from './ColorPicker.scss';

class ColorPicker extends Component {
  state = {
    chosenColor: '#ffffff',
    displayColorPicker: false,
  };

  handleColorChange = ({ hex }) => {
    this.setState({ chosenColor: hex });
  };

  toggleColorPicker = () => {
    this.setState({ displayColorPicker: !this.state.displayColorPicker });
  };

  render() {
    const { displayColorPicker, chosenColor } = this.state;

    return (
      <div>
        <button
          className={styles['color-picker--button']}
          onClick={this.toggleColorPicker}
        >
          <i
            className={classNames(
              'fas fa-palette fa-fw',
              styles['color-picker--button--icon']
            )}
          />
        </button>
        {displayColorPicker && (
          <ChromePicker
            color={chosenColor}
            onChangeComplete={this.handleColorChange}
          />
        )}
      </div>
    );
  }
}

export default ColorPicker;
