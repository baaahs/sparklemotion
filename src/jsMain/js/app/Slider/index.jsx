import React from 'react';
import PropTypes from 'prop-types';

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
        console.log('I GOT CALLED', sliderValue, this.state.sliderValue);
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
    return (
      <div>
        <input
          type="range"
          id="start"
          name="volume"
          min="0"
          max="1"
          value={this.state.sliderValue}
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
