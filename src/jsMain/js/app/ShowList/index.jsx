import React, { Component, Fragment } from 'react';
import PropTypes from 'prop-types';
import Show from './Show.jsx';
import styles from './ShowList.scss';

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
      (response) => {
        const availableShows = response.toArray();

        this.setState({ availableShows }, () => {
          const { availableShows } = this.state;
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
      <div className={styles['show-list--wrapper']}>
        <div className={styles['show-list--title']}>Shows</div>
        <ul className={styles['show-list']}>
          {availableShows.map((showName) => (
            <Show
              key={`show-${showName}`}
              name={showName}
              isSelected={showName === selectedShow}
              handleSelectShow={this.handleSelectShow}
            />
          ))}
        </ul>
      </div>
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
