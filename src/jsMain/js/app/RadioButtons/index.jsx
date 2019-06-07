import React from 'react';
import PropTypes from 'prop-types';
import sass from './RadioButtons.scss';

class RadioButtons extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      gadget: props.gadget,
    };

    props.gadget.listen({onChanged: () => {this.forceUpdate()}});
  }

  componentDidMount() {
  }

  handleButtonChange = (event) => {
    const { gadget } = this.state;
    gadget.value = event.target.value;
    this.setState({ gadget });
  };

  render() {
    const { gadget } = this.state;

    return (
      <div className={sass['radiobuttons--wrapper']}>
        <label className={sass['radiobuttons--label']} htmlFor="range-slider">
          {gadget.name}: {gadget.value}
        </label>
        {gadget.options.toArray().map ((option) => (
        <div
          key={option}>
            <input
              type="radio"
              value={option}
              step=".01"
              onChange={this.handleButtonChange.bind(this)}
            />
            <label>{option}</label>
        </div>
        ))}
      </div>
    );
  }
}

RadioButtons.propTypes = {
  pubSub: PropTypes.object,
//   gadget: PropTypes.object,
};

RadioButtons.defaultProps = {
  pubSub: {},
//   gadget: {},
};

export default RadioButtons;
