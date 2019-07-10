import React from 'react';
import PropTypes from 'prop-types';

class EyeControls extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      x: 0,
      y: 0,
      color: 'white',
    };
  }

  handlePanTiltChange = (event, axis) => {
    console.log(
      `setting side: ${this.props.side} | ${axis}: ${event.target.value}`
    );
    console.log(
      'Maybe make server call here to send updated eye value to Pinky?'
    );
    this.setState({ [axis]: event.target.value });
  };

  render() {
    return (
      <div>
        <div>
          <label htmlFor="x">X:</label>
          <input
            name="x"
            type="range"
            min="-100"
            max="100"
            value={this.state.x}
            onChange={(event) => {
              this.handlePanTiltChange(event, 'x');
            }}
          />
          {this.state.x}
        </div>
        <div>
          <label htmlFor="y">Y:</label>
          <input
            name="y"
            type="range"
            min="-100"
            max="100"
            value={this.state.y}
            onChange={(event) => {
              this.handlePanTiltChange(event, 'y');
            }}
          />
          {this.state.y}
        </div>
        <div>Eye Color: {this.state.color}</div>
      </div>
    );
  }
}

EyeControls.propTypes = {
  side: PropTypes.oneOf(['party', 'business']).isRequired,
  pubSub: PropTypes.shape().isRequired,
};

export default EyeControls;
