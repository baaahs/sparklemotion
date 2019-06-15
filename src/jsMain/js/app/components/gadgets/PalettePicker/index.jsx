import React from 'react';
import ColorWheel from '../../ColorWheel';

class PalettePicker extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      colors: this.props.gadget.colors,
    };
  }

  componentDidMount() {
    this.props.gadget.listen(this._handleChangeFromServer);
  }

  componentWillUnmount() {
    this.props.gadget.unlisten(this._handleChangeFromServer);
  }

  _handleChangeFromUi = (colors) => {
    this.setState({ colors });
    this.props.gadget.colors = colors;
  };

  _handleChangeFromServer = () => {
    this.setState({ colors: this.props.gadget.colors });
  };

  render() {
    const { colors } = this.state;

    return <ColorWheel colors={colors} onChange={this._handleChangeFromUi} />;
  }
}

export default PalettePicker;
