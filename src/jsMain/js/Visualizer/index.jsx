import React, { Component } from 'react';
import PropTypes from 'prop-types';
import styles from './visualizer.scss';

import ThreeVisualizer from './ThreeVisualizer';

export default class Visualizer extends Component {
  constructor(props) {
    super(props);

    this.threeRootElement = React.createRef();
  }

  componentDidMount() {
    this.threeVisualizer = new ThreeVisualizer(
      this.threeRootElement.current,
      this.props.sheepSimulator.sheepModel,
      [] // this.props.sheepSimulator.frameListeners,
    );

    this.props.sheepSimulator.visualize(this.threeVisualizer);
  }

  render() {
    return (
      <div className={styles['three--container']} ref={this.threeRootElement} />
    );
  }
}

Visualizer.propTypes = {
  sheepSimulator: PropTypes.shape({
    sheepModel: PropTypes.shape(),
    frameListeners: PropTypes.shape(),
  }).isRequired,
};
