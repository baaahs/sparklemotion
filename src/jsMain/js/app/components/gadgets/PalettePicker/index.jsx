import React from 'react';
import ColorWheel from '../../ColorWheel';
import { kotlin } from 'sparklemotion';

class PalettePicker extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      colors: props.gadget.colors.toArray(),
    };
  }

  componentDidMount() {
    this.props.gadget.listen(this._handleChangeFromServer);
  }

  componentWillUnmount() {
    this.props.gadget.unlisten(this._handleChangeFromServer);
  }

  _handleChangeFromUi = (colors) => {
    console.log(`kotlin`,kotlin);
    this.setState({ colors });
    this.props.gadget.colors = new kotlin.collections.ArrayList(colors);
  };

  _handleChangeFromServer = () => {
    this.setState({ colors: this.props.gadget.colors.toArray() });
  };

  render() {
    const { colors } = this.state;

    return (
      <ColorWheel
        colors={colors}
        onChange={this._handleChangeFromUi}
        isPalette
      />
    );
  }
}

export default PalettePicker;
