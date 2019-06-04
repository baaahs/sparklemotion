import React, {Component} from "react";
import styles from "./app.scss";
import ColorPicker from "./components/gadgets/ColorPicker";
import RangeSlider from "./components/gadgets/Slider";

export default class ShowControls extends Component {
  render() {
    const { gadgets } = this.props;

    return (
        <div className={styles.root}>
          {gadgets.map((gadgetInfo) => {
            const { gadget, topicName } = gadgetInfo;
            return (
                <div key={topicName} className={styles['gadget-view']}>
                  {this.createGadgetView(gadget)}
                </div>
            );
          })}
        </div>
    );
  }

  createGadgetView = (gadget) => {
    if (gadget instanceof baaahs.gadgets.ColorPicker) {
      return <ColorPicker gadget={gadget} />;
    } else if (gadget instanceof baaahs.gadgets.Slider) {
      return <RangeSlider gadget={gadget} />;
    } else if (gadget instanceof baaahs.gadgets.RadioButtons) {
      return <RadioButtons gadget={gadget}/>;
    } else {
      return <div />;
    }
  };
}
