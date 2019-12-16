import React from 'react';
import SwipeableDrawer from '@material-ui/core/SwipeableDrawer';

import ShowList from '../ShowList';
import ShowControls from './ShowControls';

import styles from './Shows.scss';

const baaahs = sparklemotion.baaahs;

class Shows extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      gadgets: [],
      showPickerOpen: false,
      selectedShow: null,
    };

    this.drawerContainerRef = React.createRef();
  }

  componentDidMount() {
    this.subscribeToChannels();
  }

  subscribeToChannels() {
    this.selectedShowChannel = this.props.pubSub.subscribe(
      baaahs.Topics.selectedShow,
      (selectedShow) => {
        this.setState({ selectedShow });
      }
    );

    this.gadgetDisplay = baaahs.GadgetDisplay(
      this.props.pubSub,
      (newGadgets) => {
        console.log('got new gadgets!', newGadgets);
        this.setState({ gadgets: newGadgets });
      }
    );
  }

  toggleShowPicker = () => {
    const { showPickerOpen } = this.state;

    this.setState({ showPickerOpen: !showPickerOpen });
  };

  handleSelectShow = (selectedShow) => {
    this.setState({ selectedShow });
    this.setSelectedShow(selectedShow);
  };

  setSelectedShow(name) {
    this.selectedShowChannel.onChange(name);
  }

  render() {
    const { showPickerOpen, gadgets, selectedShow } = this.state;
    const { pubSub } = this.props;

    return (
      <div
        ref={this.drawerContainerRef}
        className={styles['drawer__container']}
      >
        <div className={styles['current-show__label']}>Current Show: {selectedShow}</div>
        <div className={styles['show-picker__wrapper']}>
          <button
            className={styles['show-picker__button']}
            onClick={this.toggleShowPicker}
          >
            <i className="far fa-list-alt" />
          </button>
        </div>
        <SwipeableDrawer
          anchor="left"
          open={showPickerOpen}
          onOpen={this.toggleShowPicker}
          onClose={this.toggleShowPicker}
          PaperProps={{
            style: {
              position: 'absolute',
              overflowX: 'hidden',
              backgroundColor: '#747F7F',
            },
          }}
          BackdropProps={{ style: { position: 'absolute' } }}
          ModalProps={{
            container: this.drawerContainerRef
              ? this.drawerContainerRef.current
              : null,
            style: { position: 'absolute' },
          }}
          variant="temporary"
        >
          <ShowList
            pubSub={pubSub}
            selectedShow={selectedShow}
            onSelect={this.handleSelectShow}
          />
        </SwipeableDrawer>

        <div className={styles['show-controls__wrapper']}>
          <ShowControls gadgets={gadgets} />
        </div>
      </div>
    );
  }
}

export default Shows;
