import React from 'react';
import ColorWheel from '../../ColorWheel';

class PalettePicker extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      colors: this.props.gadget.colors.toArray(),
    }
  }

  _handleColorChange = (colors) => {
    this.setState({ colors });
  };

  componentDidMount() {
    this.props.gadget.listen(this._serverChangeListener);
  }

  componentWillUnmount() {
    this.props.gadget.unlisten(this._serverChangeListener);
  }

  _serverChangeListener = () => {
    this.setState({ colors: this.props.gadget.colors.toArray() });
  };

  render() {
    const { colors } = this.state;

    return (
      <ColorWheel
        colors={colors}
        onChange={this._handleColorChange}
      />
    );
  }
}

export default PalettePicker;
