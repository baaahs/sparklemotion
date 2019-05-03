import React, { Component } from 'react';
import PropTypes from 'prop-types';
import Show from './Show.jsx';

class ShowList extends Component {
  state = {
    availableShows: [],
    selectedShow: null,
  };

  componentDidMount() {
    this.subscribeToChannels();
  }

  subscribeToChannels() {
    this.selectedShowChannel = this.props.pubSub.subscribe(
      sparklemotion.baaahs.Topics.selectedShow,
      (selectedShow) => {
        this.setState({ selectedShow });
      }
    );

    this.props.pubSub.subscribe(
      sparklemotion.baaahs.Topics.availableShows,
      (availableShows) => {
        this.setState({ availableShows }, () => {
          const { availableShows } = this.state;
          console.log({ availableShows });
        });
      }
    );
  }

  handleSelectShow = (selectedShow) => {
    this.setState({ selectedShow });
    this.setSelectedShow(selectedShow);
  };

  setSelectedShow(name) {
    this.selectedShowChannel.onChange(name);
  }

  render() {
    const { selectedShow, availableShows } = this.state;

    return (
      <ul>
        {availableShows.map((showName) => (
          <Show
            key={`show-${showName}`}
            name={showName}
            isSelected={showName === selectedShow}
            handleSelectShow={this.handleSelectShow}
          />
        ))}
      </ul>
    );
  }
}

ShowList.propTypes = {
  pubSub: PropTypes.object,
};

ShowList.defaultProps = {
  pubSub: {},
};

export default ShowList;
