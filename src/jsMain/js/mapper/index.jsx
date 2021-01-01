import React, {Component} from 'react';

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
}

export default Mapper;