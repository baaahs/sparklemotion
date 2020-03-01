import React, {Component} from 'react';
import * as THREE from 'three';
import CameraControls from 'camera-controls';

CameraControls.install({THREE: THREE});

class Mapper extends Component {
    constructor(props) {
        super(props);

        this.contentRef = React.createRef();
    }

    componentDidMount = () => {
        this.props.render(this.contentRef.current);
    };

    render() {
        return (
            <div ref={this.contentRef}/>
        );
    }

    static createCameraControls(uiCamera, domElement) {
        return new CameraControls(uiCamera, domElement)
    }
}

export default Mapper;