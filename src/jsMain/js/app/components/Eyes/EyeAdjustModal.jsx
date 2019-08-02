import React, { Fragment } from 'react';
import PropTypes from 'prop-types';
import GridSlider from '../GridSlider';

import s from './Eyes.scss';

const baaahs = sparklemotion.baaahs;

class EyeAdjustModal extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      movingHeads: [],
      selected: [],
      gridSliderPosition: [127, 127],
    };

    this.pubSub = props.pubSub;

    this.movingHeadDisplay = new baaahs.MovingHeadDisplay(
      this.props.pubSub,
      (movingHeads) => {
        this.setState({ movingHeads, selected: [movingHeads[0]] });
      }
    );
  }

  handleSave = () => {
    this.props.onSave(this.state);
  };

  handlePanTiltChange = ({ x, y }) => {
    this.setState({ gridSliderPosition: [x, y] });
    this.state.selected.forEach((movingHead) => {
      movingHead.position = new baaahs.MovingHead.MovingHeadPosition(x, y);
    });
  };

  handleSelectMovingHead = (e) => {
    const { selected } = this.state;
    const toggledHead = this.state.movingHeads.find(
      (head) => head.name === e.target.value
    );
    if (e.target.checked) {
      this.setState({ selected: [...selected, toggledHead] });
    } else {
      this.setState({
        selected: [
          ...this.state.selected.filter((head) => head !== toggledHead),
        ],
      });
    }
  };

  renderRadioButtons = () => {
    const { movingHeads, selected } = this.state;
    return (
      <div>
        {movingHeads.map((movingHead) => {
          const { name } = movingHead;
          return (
            <Fragment key={name}>
              <label htmlFor={name}>{name}</label>
              <input
                type="checkbox"
                id={name}
                value={name}
                checked={selected.indexOf(movingHead) > -1}
                onChange={this.handleSelectMovingHead}
              />
            </Fragment>
          );
        })}
      </div>
    );
  };

  renderEyeControls = () => {
    const {
      gridSliderPosition: [x, y],
    } = this.state;

    return (
      <Fragment>
        <GridSlider
          className={s['grid-slider']}
          onChange={(e, data) => {
            this.handlePanTiltChange(data);
          }}
          x={x}
          y={y}
          height={255}
          width={255}
        />
        <div>Eye Color goes here</div>
      </Fragment>
    );
  };

  render() {
    return (
      <div style={{ width: '100%' }}>
        {this.renderRadioButtons()}
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
