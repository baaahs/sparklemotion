import {hot} from 'react-hot-loader';
import React, {Component, Fragment} from 'react';
import PropTypes from 'prop-types';
import SwipeableDrawer from '@material-ui/core/SwipeableDrawer';
import ShowList from './ShowList';
import ShowControls from './ShowControls';

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
        <div id="drawer-container" style={{ position: 'relative' }}>
          <div
            style={{
              position: 'absolute',
              width: '2em',
              height: '-webkit-fill-available',
              backgroundImage: "linear-gradient(to bottom right, #2F2F2F, #474747)"
            }}
            onClick={this.toggleShowPicker}
          >
            <div
              style={{
                transform: 'rotate(90deg)',
              }}
            >
              Shows
            </div>
          </div>
          <SwipeableDrawer
            anchor="left"
            open={showPickerOpen}
            onOpen={this.toggleShowPicker}
            onClose={this.toggleShowPicker}
            PaperProps={{ style: { position: 'absolute' } }}
            BackdropProps={{ style: { position: 'absolute' } }}
            ModalProps={{
              container: document.getElementById('drawer-container'),
              style: { position: 'absolute' },
            }}
            variant="temporary"
          >
            <ShowList pubSub={pubSub} onSelect={this.toggleShowPicker} />
          </SwipeableDrawer>

          <div style={{
            marginLeft: '2em',
            backgroundImage: "linear-gradient(to bottom right, #3F3F3F, #575f5f)"
          }}>
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
