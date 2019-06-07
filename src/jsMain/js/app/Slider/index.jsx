import React from 'react';
import PropTypes from 'prop-types';
import sass from './Slider.scss';
import { Slider, Handles, Tracks, Rail, Ticks } from 'react-compound-slider';
import { Handle, Track, Tick, SliderRail } from './slider-parts';

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

    let values = [props.gadget.value];
    this.state = {
      gadget: props.gadget,
      values: values,
      update: values,
    };

    props.gadget.listen({
      onChanged: () => {
        this.forceUpdate();
      },
    });
  }

  onUpdate = (update) => {
    this.setState({ update });
  };

  onChange = (values) => {
    const { gadget } = this.props;
    this.setState({ values });
    gadget.value = 1 - values[0];
  };

  handleSliderChange = (event) => {
    const { gadget } = this.state;
    gadget.value = event.target.value;
    this.setState({ gadget });
  };

  render() {
    const { gadget, values, update } = this.state;

    return (
      <div className={sass['slider--wrapper']}>
        <label className={sass['slider--label']} htmlFor="range-slider">
          {gadget.name}
        </label>
        <Slider
          vertical
          mode={2}
          step={0.01}
          domain={domain}
          rootStyle={sliderStyle}
          onUpdate={this.onUpdate}
          onChange={this.onChange}
          values={values}
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
          <Ticks count={5}>
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
