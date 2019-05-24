import React, {Component} from 'react';
import {SketchPicker} from 'react-color';

import styles from './ColorPicker.scss';

const baaahs = sparklemotion.baaahs;

class ColorPicker extends Component {
  constructor(props) {
    super(props);

    this.state = {
      gadget: props.gadget,
    };

    props.gadget.listen({onChanged: () => {this.forceUpdate()}});
  }

  handleColorChange = ({ hex }) => {
    const { gadget } = this.state;
    gadget.color = baaahs.Color.Companion.fromString(hex);
    this.setState({ gadget });
  };

  render() {
    const { gadget } = this.state;

    return (
      <div className={styles['color-picker--pad']}>
        <header>{gadget.name}</header>
        <SketchPicker
            color={gadget.color.toHexString()}
            onChangeComplete={this.handleColorChange}
        />
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
