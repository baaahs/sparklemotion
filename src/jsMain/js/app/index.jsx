import { hot } from 'react-hot-loader';
import React, { Component, Fragment } from 'react';
import PropTypes from 'prop-types';
import SwipeableDrawer from '@material-ui/core/SwipeableDrawer';
import ShowList from './ShowList';
import ShowControls from './ShowControls';
import styles from './app.scss';

const baaahs = sparklemotion.baaahs;

class App extends Component {
  constructor(props) {
    super(props);

    this.state = {
      primaryColor: baaahs.Color.Companion.WHITE,
      showPickerOpen: false,
      gadgets: [],
    };

    this.pubSub = props.pubSub;
  }

  componentDidMount() {
    this.subscribeToChannels();
  }

  subscribeToChannels() {
    this.gadgetDisplay = baaahs.GadgetDisplay(
      this.props.pubSub,
      (newGadgets) => {
        console.log('got new gadgets!', newGadgets);
        this.setState({ gadgets: newGadgets });
      }
    );
  }

  close = () => {
    console.log('app closed!');
  };

  toggleShowPicker = (event) => {
    const { showPickerOpen } = this.state;
    console.log('set showPickerOpen to', !showPickerOpen);
    this.setState({ showPickerOpen: !showPickerOpen });
  };

  render() {
    const { pubSub } = this.props;
    const { showPickerOpen, gadgets } = this.state;

    return (
      <Fragment>
        <div id="drawer-container" className={styles['drawer-container']}>
          <div
            className={styles['show-picker--button']}
            onClick={this.toggleShowPicker}
          >
            <div className={styles['show-picker--label']}>Shows</div>
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
                backgroundColor: '#575f5f',
              },
            }}
            BackdropProps={{ style: { position: 'absolute' } }}
            ModalProps={{
              container: document.getElementById('drawer-container'),
              style: { position: 'absolute' },
            }}
            variant="temporary"
          >
            <ShowList pubSub={pubSub} onSelect={this.toggleShowPicker} />
          </SwipeableDrawer>

          <div
            style={{
              marginLeft: '2em',
              backgroundImage:
                'linear-gradient(to bottom right, #3F3F3F, #575f5f)',
            }}
          >
            <ShowControls gadgets={gadgets} />
          </div>
        </div>
      </Fragment>
    );
  }
}

App.propTypes = {
  pubSub: PropTypes.object.isRequired,
};

export default hot(module)(App);
