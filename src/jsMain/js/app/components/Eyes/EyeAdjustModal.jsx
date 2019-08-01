import React, { Fragment } from 'react';
import PropTypes from 'prop-types';
import GridSlider from '../GridSlider';

import s from './Eyes.scss';

const baaahs = sparklemotion.baaahs;

class EyeAdjustModal extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      currentEye: 'leftEye',
      leftEye: [50, 50],
      rightEye: [50, 50],
      partEyeColor: 'white',
      rightEyeColor: 'white',
    };

    this.pubSub = props.pubSub;
  }

  componentDidMount() {
    this.subscribeToChannels();
  }

  subscribeToChannels() {
    this.movingHeadDisplay = new baaahs.MovingHeadDisplay(this.props.pubSub);
    this.leftEyeChannel = this.movingHeadDisplay.subscribe(
      'leftEye',
      ({ x, y }) => {
        this.setState({ leftEye: [x, y] });
      }
    );

    this.rightEyeChannel = this.movingHeadDisplay.subscribe(
      'rightEye',
      ({ x, y }) => {
        this.setState({ rightEye: [x, y] });
      }
    );
  }

  handleSave = () => {
    this.props.onSave(this.state);
  };

  handlePanTiltChange = (eyeSide, { x, y }) => {
    if (eyeSide === 'leftEye') {
      this.leftEyeChannel.onChange({
        x,
        y,
      });
    }
    this.setState({ [eyeSide]: [x, y] });
  };

  handleEyeToggle = (e) => {
    this.setState({
      currentEye: e.target.checked ? 'rightEye' : 'leftEye',
    });
  };

  renderEyeControls = () => {
    const {
      currentEye,
      leftEye,
      rightEye,
      partEyeColor,
      rightEyeColor,
    } = this.state;
    const [x, y] = currentEye === 'leftEye' ? leftEye : rightEye;
    const eyeColor = currentEye === 'leftEye' ? partEyeColor : rightEyeColor;

    return (
      <Fragment>
        <GridSlider
          className={s['grid-slider']}
          onChange={(e, data) => {
            this.handlePanTiltChange(currentEye, data);
          }}
          x={x}
          y={y}
          height={255}
          width={255}
        />
        <div>Eye Color: {eyeColor}</div>
      </Fragment>
    );
  };

  render() {
    return (
      <div style={{ width: '100%' }}>
        <div className={s['toggle-switch__wrapper']}>
          <span>Left</span>
          <div className={s['toggle-switch']}>
            <input
              type="checkbox"
              id="toggle-switch"
              checked={this.state.currentEye === 'rightEye'}
              onChange={this.handleEyeToggle}
            />
            <label htmlFor="toggle-switch">Eye Toggle</label>
          </div>
          <span>Right</span>
        </div>
        {this.renderEyeControls()}
        <button onClick={this.handleSave}>SAVE</button>
      </div>
    );
  }
}

EyeAdjustModal.propTypes = {
  onSave: PropTypes.func.isRequired,
  pubSub: PropTypes.instanceOf(Object).isRequired,
};

export default EyeAdjustModal;
