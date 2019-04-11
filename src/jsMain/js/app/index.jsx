import React, {Component} from 'react';
import ColorPicker from "./Menu/components/ColorPicker";

class App extends Component {
  constructor(props) {
    super(props);
    this.state = {primaryColor: sparklemotion.baaahs.Color.Companion.WHITE};
    this.pubSub = this.props.uiContext.pubSub;
  }

  componentDidMount() {
    this.primaryColorChannel =
        this.pubSub.subscribe(sparklemotion.baaahs.Topics.primaryColor, (color) => {
          console.log("received updated primary color!", color, this);
          this.setState({primaryColor: color});
        });
  }

  colorChanged = color => {
    console.log("primary color selected!", color, this);
    this.primaryColorChannel.onChange(sparklemotion.baaahs.Color.Companion.fromString(color));
  };

  render() {
    return <div>
      <ColorPicker chosenColor={this.state.primaryColor.toHexString()} onColorSelect={this.colorChanged}/>
    </div>;
  }
}

export default App;
