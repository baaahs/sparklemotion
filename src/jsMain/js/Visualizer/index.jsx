import React, { Component } from 'react';
import PropTypes from 'prop-types';

// todo: this isn't quite right, somehow encapsulate THREE rendering stuff here too?
export default class Visualizer extends Component {
  constructor(props) {
    super(props);

    this.threeRootElement = React.createRef();
  }

  componentDidMount() {
    initThreeJs(
      this.threeRootElement.current,
      this.props.model.sheepModel,
      this.props.model.frameListeners
    );
  }

  render() {
    return (
      <div
        className="three--container"
        ref={this.threeRootElement}
      />
    );
  }
}

Visualizer.propTypes = {
  model: PropTypes.shape().isRequired,
};
