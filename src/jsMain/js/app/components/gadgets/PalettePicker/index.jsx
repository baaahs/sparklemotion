import React from 'react';
import ColorWheel from '../../ColorWheel';

class PalettePicker extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      colors: this.props.gadget.colors,
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
    this.setState({ colors });
  };

  render() {
    return (
      <ColorWheel
        colors={this.props.gadget.colors}
        onChange={this._handleColorChange}
      />
    );
  }
}

export default PalettePicker;
