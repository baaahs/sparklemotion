import React, {Component} from 'react';
import PropTypes from 'prop-types';

// todo: this isn't quite right, somehow encapsulate THREE rendering stuff here too?
export default class Visualizer extends Component {
    constructor(props) {
        super(props);
    }

    componentDidMount() {
        initThreeJs(this.threeRootElement,
            this.props.model.sheepModel,
            this.props.model.frameListeners);
    }

    render() {
        return (
            <div
                className="three--container"
                ref={el => (this.threeRootElement = el)}
            />
        );
    }
}

Visualizer.propTypes = {
  model: PropTypes.shape().isRequired,
};