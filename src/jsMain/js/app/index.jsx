import React, { Component } from 'react';
import ColorPicker from './Menu/components/ColorPicker';

class App extends Component {
  constructor(props) {
    super(props);
    this.state = { primaryColor: sparklemotion.baaahs.Color.Companion.WHITE };
    this.pubSub = props.uiContext.pubSub;
  }

  componentDidMount() {
    this.primaryColorChannel = this.pubSub.subscribe(
      sparklemotion.baaahs.Topics.primaryColor,
      (primaryColor) => {
        console.log('received updated primary color!', primaryColor, this);
        this.setState({ primaryColor });
      }
    );
  }

  colorChanged = (color) => {
    console.log('primary color selected!', color, this);
    this.primaryColorChannel.onChange(
      sparklemotion.baaahs.Color.Companion.fromString(color)
    );
  };

  close = () => {
    console.log("app closed!");
  };

  render() {
    return (
      <div>
        <ColorPicker
          chosenColor={this.state.primaryColor}
          onColorSelect={this.colorChanged}
        />
      </div>
    );
  }
}

export default App;
