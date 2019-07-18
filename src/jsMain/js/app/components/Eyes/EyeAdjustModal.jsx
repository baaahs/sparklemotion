import React from 'react';
import PropTypes from 'prop-types';

class EyeAdjustModal extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      x: 50,
      y: 50,
      color: 'white',
    };
  }

  handleSave = () => {
    this.props.onSave(this.state);
  }

  handlePanTiltChange = (event, axis) => {
    this.setState({ [axis]: event.target.value });
  };

  render() {
    return (
      <div>
        <div>{this.props.title}</div>
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
        <button onClick={this.handleSave}>SAVE</button>
      </div>
    );
  }
}

EyeAdjustModal.propTypes = {
  onSave: PropTypes.func.isRequired,
  title: PropTypes.string.isRequired,
}

export default EyeAdjustModal;
