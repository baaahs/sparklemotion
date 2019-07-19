import React, { Fragment } from 'react';
import PropTypes from 'prop-types';
import GridSlider from '../GridSlider';

import s from './Eyes.scss';

class EyeAdjustModal extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      currentEye: 'partyEye',
      partyEye: [50, 50],
      businessEye: [50, 50],
      partEyeColor: 'white',
      businessEyeColor: 'white',
    };
  }

  handleSave = () => {
    this.props.onSave(this.state);
  };

  handlePanTiltChange = (eyeSide, { x, y }) => {
    this.setState({ [eyeSide]: [x, y] });
  };

  handleEyeToggle = (e) => {
    this.setState({
      currentEye: e.target.checked ? 'businessEye' : 'partyEye',
    });
  };

  renderEyeControls = () => {
    const {
      currentEye,
      partyEye,
      businessEye,
      partEyeColor,
      businessEyeColor,
    } = this.state;
    const [x, y] = currentEye === 'partyEye' ? partyEye : businessEye;
    const eyeColor =
      currentEye === 'partyEye' ? partEyeColor : businessEyeColor;

    return (
      <Fragment>
        <GridSlider
          className={s['grid-slider']}
          onChange={(e, data) => {
            this.handlePanTiltChange(currentEye, data);
          }}
          x={x}
          y={y}
        />
        <div>Eye Color: {eyeColor}</div>
      </Fragment>
    );
  };

  render() {
    return (
      <div style={{ width: '100%' }}>
        <div className={s['toggle-switch__wrapper']}>
          <span>Party</span>
          <div className={s['toggle-switch']}>
            <input
              type="checkbox"
              id="toggle-switch"
              checked={this.state.currentEye === 'businessEye'}
              onChange={this.handleEyeToggle}
            />
            <label htmlFor="toggle-switch">Eye Toggle</label>
          </div>
          <span>Business</span>
        </div>
        {this.renderEyeControls()}
        <button onClick={this.handleSave}>SAVE</button>
      </div>
    );
  }
}

EyeAdjustModal.propTypes = {
  onSave: PropTypes.func.isRequired,
};

export default EyeAdjustModal;
