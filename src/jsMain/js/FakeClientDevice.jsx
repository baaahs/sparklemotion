import React, {Component} from 'react';
import Draggable from 'react-draggable';
import styles from './FakeClientDevice.scss';

export default class FakeClientDevice extends Component {
  constructor(props) {
    super(props);
    this.width = props.width / 2;
    this.height = props.height / 2;
    this.onClose = props.onClose;
    this.onResize = props.onResize;
  }

  onZoomOut = () => {};
  onZoomIn = () => {};

  render() {
    const borderWidth = 10;
    const containerStyle = {
      width: (this.width + borderWidth * 2) + 'px',
      height: (this.height + borderWidth * 2) + 'px',
    };
    const contentStyle = {
      width: this.width + 'px',
      height: this.height + 'px',
    };

    return <Draggable>
      <div className={styles['FakeClientDevice--pad']} style={containerStyle}>
        <div className={styles['FakeClientDevice-controls']}>
          <i className="fas fa-search-minus" onClick={_ => this.onZoomOut()}/>
          <i className="fas fa-search-plus" onClick={_ => this.onZoomIn()}/>
          &nbsp;
          <i className="far fa-times-circle" onClick={_ => this.onClose()}/>
        </div>
        <div className={styles['FakeClientDevice--content']} style={contentStyle}/>
      </div>
    </Draggable>;
  }

}