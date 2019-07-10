import React from 'react';
import EyeControls from '../EyeControls';

class Eyes extends React.Component {
  constructor(props) {
    super(props);
  }

  render() {
    const { pubSub } = this.props;

    return (
      <div>
        <span>Eye Controls</span>
        <EyeControls side="party" pubSub={pubSub} />
        <EyeControls side="business" pubSub={pubSub} />
      </div>
    )
  }
}

export default Eyes;
