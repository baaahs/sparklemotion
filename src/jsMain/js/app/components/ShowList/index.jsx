import React, {Component} from 'react';
import PropTypes from 'prop-types';
import Show from './Show';
import styles from './ShowList.scss';

import {baaahs} from 'sparklemotion';

class ShowList extends Component {
  state = {
    availableShows: [],
  };

  componentDidMount() {
    this.subscribeToChannels();
  }

  subscribeToChannels() {
    this.props.pubSub.subscribe(baaahs.Topics.availableShows, (response) => {
      const availableShows = response.toArray();

      this.setState({ availableShows });
    });
  }

  render() {
    const { availableShows } = this.state;
    const { selectedShow } = this.props;

    return (
      <div className={styles['show-list--wrapper']}>
        <div className={styles['show-list--title']}>Show List</div>
        <ul className={styles['show-list']}>
          {availableShows.map((showName) => (
            <Show
              key={`show-${showName}`}
              name={showName}
              isSelected={showName === selectedShow}
              handleSelectShow={() => {
                this.props.onSelect(showName);
              }}
            />
          ))}
        </ul>
      </div>
    );
  }
}

ShowList.propTypes = {
  pubSub: PropTypes.object,
  selectedShow: PropTypes.string,
};

ShowList.defaultProps = {
  pubSub: {},
  selectedShow: '',
};

export default ShowList;
