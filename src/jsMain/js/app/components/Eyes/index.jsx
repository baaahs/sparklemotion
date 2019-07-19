import React, { Fragment } from 'react';
import Dialog from '@material-ui/core/Dialog';
import EyeControls from '../EyeControls';
import Modal from '../Modal';
import { PRESET_HEADLIGHT_MODE, PRESET_DISCOBALL_MODE } from '../../constants';
import EyeAdjustModal from './EyeAdjustModal';

class Eyes extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      isModalOpen: false,
      modalToDisplay: null,
    };
  }

  handleSelectHeadlightPreset = (preset) => {
    console.log(`Should make server call to set preset to ${preset}`);
  };

  handleEditHeadlightPreset = (preset) => {
    const modalToOpen = (
      <EyeAdjustModal
        onSave={(settings) => {
          this.handleSaveHeadlightPreset(preset, settings);
        }}
      />
    );

    this.setState({ isModalOpen: true, modalToDisplay: modalToOpen });
  };

  handleSaveHeadlightPreset = (preset, settings) => {
    console.log(
      `Should make server call to set preset to ${preset}, with settings: ${JSON.stringify(
        settings
      )}`
    );
  };

  handleCloseModal = () => {
    this.setState({ isModalOpen: false, modalToDisplay: null });
  };

  render() {
    const { pubSub } = this.props;

    return (
      <div>
        <span>Eye Controls</span>
        <EyeControls side="party" pubSub={pubSub} />
        <EyeControls side="business" pubSub={pubSub} />
        <div>
          <span>Eye Presets</span>
          <div>
            <button
              onClick={() => {
                this.handleSelectHeadlightPreset(PRESET_HEADLIGHT_MODE);
              }}
            >
              Headlight Mode
            </button>
            <button
              onClick={() => {
                this.handleEditHeadlightPreset(PRESET_HEADLIGHT_MODE);
              }}
            >
              Edit
            </button>
          </div>
          <div>
            <button
              onClick={() => {
                this.handleSelectHeadlightPreset(PRESET_DISCOBALL_MODE);
              }}
            >
              Discoball Mode
            </button>
            <button
              onClick={() => {
                this.handleEditHeadlightPreset(PRESET_DISCOBALL_MODE);
              }}
            >
              Edit
            </button>
          </div>
        </div>

        <Modal
          isOpen={this.state.isModalOpen}
          onClose={this.handleCloseModal}
          size="large"
        >
          {this.state.modalToDisplay}
        </Modal>
      </div>
    );
  }
}

export default Eyes;
