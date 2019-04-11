import React, {Component} from 'react';
import classNames from 'classnames';
import {SketchPicker} from 'react-color';

import styles from './ColorPicker.scss';

class ColorPicker extends Component {
  constructor(props) {
    super(props);

    this.onColorSelect = props.onColorSelect;

    this.state = {
      chosenColor: props.chosenColor,
      displayColorPicker: true,
    };
  }

  handleColorChange = ({ hex }) => {
    this.setState({ chosenColor: hex });
    if (this.onColorSelect) {
      this.onColorSelect(hex);
    }
  };

  toggleColorPicker = () => {
    this.setState({ displayColorPicker: !this.state.displayColorPicker });
  };

  render() {
    const { displayColorPicker, chosenColor } = this.state;

    return (
      <div className={styles['color-picker--pad']}>
        <header>Primary Color</header>
        {!displayColorPicker && (
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
        )}
        {displayColorPicker && (
          <SketchPicker
            color={chosenColor}
            onChangeComplete={this.handleColorChange}
          />
        )}
      </div>
    );
  }
}

export default ColorPicker;
