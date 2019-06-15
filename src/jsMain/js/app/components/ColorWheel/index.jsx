import React, { Component } from 'react';
import PropTypes from 'prop-types';
import styles from './ColorWheel.scss';
import classNames from 'classnames';
import chroma from 'chroma-js';
import throttle from 'lodash/throttle';
import Draggable from 'react-draggable';
import { rgb2xy, xy2polar, xy2rgb } from '../../../utils/colorUtils';

const baaahs = sparklemotion.baaahs;

const pickerRadius = 12;

const HARMONY_MODES = {
  custom: 'custom',
  triad: 'triad',
  analogous: 'analogous',
};

function fromColorsToRgbArrays(colors) {
  return colors.map(fromColorToRgbArray);
}

function fromRgbArraysToColors(rgbArrays) {
  return rgbArrays.map(fromRgbArrayToColor);
}
function fromColorToRgbArray(color) {
  return [color.redI, color.greenI, color.blueI];
}

function fromRgbArrayToColor(rgbArray) {
  return baaahs.Color.Companion.fromInts(...rgbArray);
}

class ColorWheel extends Component {
  constructor(props) {
    super(props);

    this.state = {
      colors: props.colors.map(fromColorsToRgbArrays),
      radius: 10,
      selectedIndex: -1,
      grabbingIndex: -1,
      clientWidth: 20,
      harmonyMode: HARMONY_MODES.custom,
    };

    // Send color updates once every 150ms while the picker is dragged
    this._throttledHandleColorChange = throttle(this._handleColorChange, 150);
  }

  updateWheel() {
    const { radius } = this.state;
    if (!this._canvasEl || !radius) return;

    const width = radius * 2;
    const height = radius * 2;
    const featheringPx = 1;

    let ctx = this._canvasEl.getContext('2d');
    let image = ctx.createImageData(width, height);
    let data = image.data;

    for (let x = -radius; x < radius; x++) {
      for (let y = -radius; y < radius; y++) {
        let [r] = xy2polar(x, y);
        // Figure out the starting index of this pixel in the image data array.
        let rowLength = 2 * radius;
        let adjustedX = x + radius; // convert x from [-radius, radius] to [0, radius*2] (the coordinates of the image data array)
        let adjustedY = y + radius; // convert y from [-radius, radius] to [0, radius*2] (the coordinates of the image data array)
        let pixelWidth = 4; // each pixel requires 4 slots in the data array
        let index = (adjustedX + adjustedY * rowLength) * pixelWidth;

        let [red, green, blue] = xy2rgb(x, y, radius);
        let alpha = 255;
        if (r >= radius - featheringPx) {
          alpha = Math.max(
            255 - 255 * ((r - (radius - featheringPx)) / featheringPx),
            0
          );
        }

        data[index] = red;
        data[index + 1] = green;
        data[index + 2] = blue;
        data[index + 3] = alpha;
      }
    }

    ctx.putImageData(image, 0, 0);
  }

  _handleColorChange = () => {
    this.props.onChange(this.state.colors.map(fromRgbArraysToColors));
  };

  _onPickerDragged(ev, data, index) {
    const { x, y } = data;
    const updatedColors = this._getUpdatedColors(x, y, index);
    this.setState(
      { selectedIndex: index, grabbingIndex: index, colors: updatedColors },
      () => this._throttledHandleColorChange()
    );
  }

  _onPickerMouseUp(ev, data, index) {
    const { x, y } = data;
    const updatedColors = this._getUpdatedColors(x, y, index);
    this.setState(
      {
        colors: updatedColors,
        grabbingIndex: -1,
      },
      () => this._handleColorChange()
    );
  }

  _getUpdatedColors(x, y, index) {
    const { radius, colors, harmonyMode } = this.state;
    const color = xy2rgb(x - radius, y - radius, radius);

    const updatedColors = colors.slice();
    updatedColors[index] = color;

    if (harmonyMode === HARMONY_MODES.triad) {
      for (let i = 1; i < colors.length; i++) {
        const nextIndex = (index + i) % colors.length;
        const [currentHue, s, v] = chroma.rgb(colors[index]).hsv();
        const nextHue = currentHue + (((360 / colors.length) * i) % 360);
        const nextColor = chroma.hsv(nextHue, s, v).rgb();
        updatedColors[nextIndex] = nextColor;
      }
    } else if (harmonyMode === HARMONY_MODES.analogous) {
      const analogousHueSpread = 90;
      const spreadStep = analogousHueSpread / colors.length;
      for (let i = 1; i < colors.length; i++) {
        const nextIndex = (index + i) % colors.length;
        const [currentHue, s, v] = chroma.rgb(colors[index]).hsv();
        let indexOffset = i + Math.floor(colors.length / 2);
        if (i >= colors.length / 2) indexOffset += 1;
        const nextHue =
          (currentHue - analogousHueSpread + spreadStep * indexOffset) % 360;
        const nextColor = chroma.hsv(nextHue, s, v).rgb();
        updatedColors[nextIndex] = nextColor;
      }
    }

    return updatedColors;
  }

  _renderPicker(picker, index) {
    const { radius, selectedIndex, grabbingIndex } = this.state;
    const { color } = picker;
    const [r, g, b] = color;
    const position = rgb2xy(color, radius);
    return (
      <Draggable
        key={index}
        defaultClassName={styles.draggablePicker}
        defaultClassNameDragging={styles.dragging}
        position={{ x: position[0], y: position[1] }}
        onDrag={(e, data) => {
          return this._onPickerDragged(e, data, index);
        }}
        onStop={(e, data) => {
          return this._onPickerMouseUp(e, data, index);
        }}
        bounds={{ top: 0, left: 0, right: radius * 2, bottom: radius * 2 }}
      >
        <div>
          <div
            style={{
              width: pickerRadius * 2,
              height: pickerRadius * 2,
              backgroundColor: `rgb(${r}, ${g}, ${b})`,
            }}
            className={classNames(styles.picker, {
              [styles.grabbing]: index === grabbingIndex,
              [styles.selected]: index === selectedIndex,
            })}
            onMouseDown={() => {
              this.setState({
                selectedIndex: index,
                grabbingIndex: index,
              });
            }}
          />
        </div>
      </Draggable>
    );
  }

  render() {
    const { radius, colors } = this.state;

    return (
      <div
        ref={(el) => {
          if (!el) return;

          const { clientWidth } = el;
          const newRadius = clientWidth / 2;
          if (radius != newRadius) {
            this.setState({ radius: newRadius }, () => this.updateWheel());
          }
        }}
        className={styles.root}
      >
        <div
          className={styles.canvasWrapper}
          style={{ width: radius * 2, height: radius * 2 }}
        >
          <canvas
            width={radius * 2}
            height={radius * 2}
            className={styles.canvas}
            ref={(el) => (this._canvasEl = el)}
            onMouseDown={() => {
              this.setState({
                selectedIndex: -1,
              });
            }}
          />
        </div>
        {radius != null &&
          colors.map((color, i) => this._renderPicker({ color }, i))}
        <div className={styles.harmonyModes}>
          {Object.keys(HARMONY_MODES).map((harmonyMode) => (
            <button
              key={harmonyMode}
              onClick={() => this.setState({ harmonyMode })}
              className={classNames(styles.harmonyMode, {
                [styles.active]: harmonyMode === this.state.harmonyMode,
              })}
            >
              {harmonyMode}
            </button>
          ))}
        </div>
      </div>
    );
  }
}

ColorWheel.propTypes = {
  colors: PropTypes.arrayOf(PropTypes.arrayOf(PropTypes.number)).isRequired,
  onChange: PropTypes.func.isRequired,
};

export default ColorWheel;
