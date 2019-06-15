import React from 'react';
import PropTypes from 'prop-types';
import throttle from 'lodash/throttle';
import sass from './Slider.scss';
import {Handles, Rail, Slider, Ticks, Tracks} from 'react-compound-slider';
import {Handle, SliderRail, Tick, Track} from './slider-parts';

const sliderStyle = {
  position: 'relative',
  height: '200px',
  marginLeft: '45%',
  touchAction: 'none',
};

const domain = [0, 1];

class RangeSlider extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      value: props.gadget.value,
    };

    props.gadget.listen(() => {
      this.setState({ value: props.gadget.value });
    });
  }

  componentWillUnmount() {
    if (this._handleChangeFromServer) {
      this.props.gadget.unlisten(this._handleChangeFromServer);
    }
  }

  onUpdate = throttle((value) => {
    this.onChange(value);
  }, 10);

  onChange = (value) => {
    this.props.gadget.value = value;
    this.setState({ value });
  };

  render() {
    const { value } = this.state;

    return (
      <div className={sass['slider--wrapper']}>
        <label className={sass['slider--label']} htmlFor="range-slider">
          {this.props.gadget.name}
        </label>
        <Slider
          vertical
          reversed
          mode={2}
          step={0.01}
          domain={domain}
          rootStyle={sliderStyle}
          onUpdate={(values) => {
            this.onUpdate(values[0]);
          }}
          onChange={(values) => {
            this.onChange(values[0]);
          }}
          values={[value]}
        >
          <Rail>
            {({ getRailProps }) => <SliderRail getRailProps={getRailProps} />}
          </Rail>
          <Handles>
            {({ handles, getHandleProps }) => (
              <div className="slider-handles">
                {handles.map((handle) => (
                  <Handle
                    key={handle.id}
                    handle={handle}
                    domain={domain}
                    getHandleProps={getHandleProps}
                  />
                ))}
              </div>
            )}
          </Handles>
          <Tracks left={false} right={false}>
            {({ tracks, getTrackProps }) => (
              <div className="slider-tracks">
                {tracks.map(({ id, source, target }) => (
                  <Track
                    key={id}
                    source={source}
                    target={target}
                    getTrackProps={getTrackProps}
                  />
                ))}
              </div>
            )}
          </Tracks>
          <Ticks count={10}>
            {({ ticks }) => (
              <div className="slider-ticks">
                {ticks.map((tick) => (
                  <Tick key={tick.id} tick={tick} />
                ))}
              </div>
            )}
          </Ticks>
        </Slider>
      </div>
    );
  }
}

RangeSlider.propTypes = {
  pubSub: PropTypes.object,
  //   gadget: PropTypes.object,
};

RangeSlider.defaultProps = {
  pubSub: {},
  //   gadget: {},
};

export default RangeSlider;
