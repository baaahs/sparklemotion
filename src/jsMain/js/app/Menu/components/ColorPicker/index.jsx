import React, { Component } from 'react';
import PropTypes from 'prop-types';
import classNames from 'classnames';
import { SketchPicker } from 'react-color';

import styles from './ColorPicker.scss';

class ColorPicker extends Component {
  constructor(props) {
    super(props);

    this.state = {
      displayColorPicker: false,
    };
  }

  handleColorChange = ({ hex }) => {
    this.props.onColorSelect(hex);
  };

  toggleColorPicker = () => {
    this.setState({ displayColorPicker: !this.state.displayColorPicker });
  };

  render() {
    const { displayColorPicker } = this.state;
    const { chosenColor } = this.props;

    return (
      <div className={styles['color-picker--pad']}>
        <header>Primary Color</header>
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
          <SketchPicker
            color={this.props.chosenColor.toHexString()}
            onChangeComplete={this.handleColorChange}
          />
        )}
      </div>
    );
  }
}

ColorPicker.propTypes = {
  chosenColor: PropTypes.shape({
    red: PropTypes.number,
    blue: PropTypes.number,
    green: PropTypes.number,
  }),
};

ColorPicker.defaultProps = {
  chosenColor: { red: 255, blue: 255, green: 255 },
};

export default ColorPicker;
