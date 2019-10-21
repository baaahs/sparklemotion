import React, { Component } from 'react';
import styles from './Shows.scss';
import ColorPicker from '../gadgets/ColorPicker';
import PalettePicker from '../gadgets/PalettePicker';
import RangeSlider from '../gadgets/Slider';

const baaahs = sparklemotion.baaahs;

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
    } else if (gadget instanceof baaahs.gadgets.PalettePicker) {
      return <PalettePicker gadget={gadget} />;
    } else if (gadget instanceof baaahs.gadgets.Slider) {
      return <RangeSlider gadget={gadget} />;
    } else {
      console.warn(
        `Warning: Attempting to render a gadget that does not exist.`,
        gadget
      );
      return null;
    }
  };
}
