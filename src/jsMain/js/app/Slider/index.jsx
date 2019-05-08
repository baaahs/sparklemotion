import React from 'react';
import PropTypes from 'prop-types';
import sass from './Slider.scss';

class Slider extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      sliderValue: 0,
    };
  }

  componentDidMount() {
    this.subscribeToChannels();
  }

  subscribeToChannels() {
    this.sliderChannel = this.props.pubSub.subscribe(
      sparklemotion.baaahs.Topics.sliderInput,
      (sliderValue) => {
        this.setState({ sliderValue });
      }
    );
  }

  handleSliderChange = (event) => {
    const sliderValue = event.target.value;

    this.setState({ sliderValue });
    this.sliderChannel.onChange(sliderValue);
  };

  render() {
    const { sliderValue } = this.state;

    return (
      <div className={sass['slider--wrapper']}>
        <label className={sass['slider--label']} htmlFor="range-slider">
          Sparkle Slider: {sliderValue}
        </label>
        <input
          type="range"
          id="start"
          name="range-slider"
          min="0"
          max="1"
          value={sliderValue}
          step=".01"
          onChange={this.handleSliderChange}
        />
      </div>
    );
  }
}

Slider.propTypes = {
  pubSub: PropTypes.object,
};

Slider.defaultProps = {
  pubSub: {},
};

export default Slider;
