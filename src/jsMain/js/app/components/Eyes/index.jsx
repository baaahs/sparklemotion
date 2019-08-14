import React, { Fragment } from 'react';
import PropTypes from 'prop-types';
import GridSlider from '../GridSlider';
import Modal from '../Modal';

import s from './Eyes.scss';

const baaahs = sparklemotion.baaahs;

class Eyes extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      movingHeads: [],
      selected: [],
      gridSliderPosition: [127, 127],
      presets: {},
      showModal: '',
    };
  }

  componentDidMount() {
    this.movingHeadDisplay = new baaahs.MovingHeadDisplay(
      this.props.pubSub,
        (movingHeads) => {
        this.setState({ movingHeads, selected: [movingHeads[0]] });
      }
    );

    this.movingHeadDisplay.addPresetsListener(this.presetListenerFn);
  }

  componentWillUnmount() {
    if (this.movingHeadDisplay) {
      this.movingHeadDisplay.removePresetsListener(this.presetListenerFn);
    }
  }

  presetListenerFn = (presetsJson) => {
    const presets = JSON.parse(presetsJson);

    this.setState({ presets: presets });
  }

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

  handlePresetSelect = (presetName) => {
    switch (this.state.showModal) {
      case 'load':
        this.loadPreset(presetName);
        break;
      case 'save':
        this.savePreset(presetName);
        break;
    }
    this.setState({ showModal: false });
  };

  loadPreset = (presetName) => {
    const { x, y } = this.state.presets[presetName];
    this.handlePanTiltChange({ x, y });
  };

  savePreset = (presetName) => {
    const [x, y] = this.state.gridSliderPosition;

    this.movingHeadDisplay.savePreset(
      presetName,
      new baaahs.MovingHead.MovingHeadPosition(x, y)
    );
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

  renderLoadModal = () => {
    return (
      <Fragment>
        {Object.keys(this.state.presets).map((presetName) => {
          return (
            <button key={presetName} onClick={() => this.handlePresetSelect(presetName)}>
              {presetName}
            </button>
          );
        })}
      </Fragment>
    );
  };

  render() {
    return (
      <Fragment>
        <div style={{ width: '100%' }}>
          <div className={s['moving-heads__wrapper']}>
            {this.renderRadioButtons()}
            {this.renderEyeControls()}
          </div>
          <div style={{ display: 'inline-block' }}>
            <button
              onClick={() => {
                this.setState({ showModal: 'save' });
              }}
            >
              SAVE
            </button>
            <button
              onClick={() => {
                this.setState({ showModal: 'load' });
              }}
            >
              LOAD
            </button>
          </div>
        </div>

        <Modal
          isOpen={!!this.state.showModal}
          onClose={() => {
            this.setState({ showModal: false });
          }}
        >
          {Object.keys(this.state.presets).map((presetName) => {
            return (
              <button
                key={presetName}
                style={{ display: "block" }}
                onClick={() => this.handlePresetSelect(presetName)}
              >
                {presetName}
              </button>
            );
          })}
          {this.state.showModal === 'save' && (
            <input
              style={{ display: "block" }}
              onBlur={(e) => {
                this.savePreset(e.target.value);
              }}
              onKeyPress={(e) => {
                if (e.key === 'Enter') {
                  this.savePreset(e.target.value);
                  e.preventDefault();
                }
              }}
            />
          )}
        </Modal>
      </Fragment>
    );
  }
}

Eyes.propTypes = {
  pubSub: PropTypes.instanceOf(Object).isRequired,
};

export default Eyes;
