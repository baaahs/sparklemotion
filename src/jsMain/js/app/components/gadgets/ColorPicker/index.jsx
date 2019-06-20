import React from 'react';
import ColorWheel from '../../ColorWheel';

class ColorPicker extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      colors: [props.gadget.color],
    };
  }

  componentDidMount() {
    this.props.gadget.listen(this._handleChangeFromServer);
  }

  componentWillUnmount() {
    this.props.gadget.unlisten(this._handleChangeFromServer);
  }

  _handleChangeFromUi = ([colors]) => {
    this.setState({ colors: [colors] });
    this.props.gadget.color = colors;
  };

  _handleChangeFromServer = () => {
    this.setState({ colors: [this.props.gadget.color] });
  };

  render() {
    return (
      <ColorWheel
        colors={this.state.colors}
        onChange={this._handleChangeFromUi}
      />
    );
  }
}

export default ColorPicker;
