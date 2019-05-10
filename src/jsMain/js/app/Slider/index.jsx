import React from 'react';
import PropTypes from 'prop-types';
import sass from './Slider.scss';

class Slider extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      gadget: props.gadget,
    };

    props.gadget.listen({onChanged: () => {this.forceUpdate()}});
  }

  componentDidMount() {
  }

  handleSliderChange = (event) => {
    const { gadget } = this.state;
    gadget.value = event.target.value;
    this.setState({ gadget });
  };

  render() {
    const { gadget } = this.state;

    return (
      <div className={sass['slider--wrapper']}>
        <label className={sass['slider--label']} htmlFor="range-slider">
          {gadget.name}: {gadget.value}
        </label>
        <input
          type="range"
          id="start"
          name="range-slider"
          min="0"
          max="1"
          value={gadget.value}
          step=".01"
          onChange={this.handleSliderChange.bind(this)}
        />
      </div>
    );
  }
}

Slider.propTypes = {
  pubSub: PropTypes.object,
//   gadget: PropTypes.object,
};

Slider.defaultProps = {
  pubSub: {},
//   gadget: {},
};

export default Slider;
