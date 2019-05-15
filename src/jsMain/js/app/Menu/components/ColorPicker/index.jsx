import React, {Component} from 'react';
import classNames from 'classnames';
import {SketchPicker} from 'react-color';

import styles from './ColorPicker.scss';

class ColorPicker extends Component {
  constructor(props) {
    super(props);

    this.state = {
      gadget: props.gadget,
      displayColorPicker: false,
    };

    props.gadget.listen({onChanged: () => {this.forceUpdate()}});
  }

  handleColorChange = ({ hex }) => {
    const { gadget } = this.state;
    gadget.color = sparklemotion.baaahs.Color.Companion.fromString(hex);
    this.setState({ gadget });
  };

  toggleColorPicker = () => {
    this.setState({ displayColorPicker: !this.state.displayColorPicker });
  };

  render() {
    const { displayColorPicker, gadget } = this.state;

    return (
      <div className={styles['color-picker--pad']}>
        <header>{gadget.name}</header>
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
            color={gadget.color.toHexString()}
            onChangeComplete={this.handleColorChange}
          />
        )}
      </div>
    );
  }
}

// ColorPicker.propTypes = {
//   chosenColor: PropTypes.shape({
//     red: PropTypes.number,
//     blue: PropTypes.number,
//     green: PropTypes.number,
//   }),
// };
//
// ColorPicker.defaultProps = {
//   chosenColor: { red: 255, blue: 255, green: 255 },
// };

export default ColorPicker;
